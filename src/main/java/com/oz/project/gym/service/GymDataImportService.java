package com.oz.project.gym.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.oz.project.api.dto.DocumentDto;
import com.oz.project.api.service.KakaoAddressSearchService;
import com.oz.project.gym.entity.Gym;
import com.oz.project.gym.repository.GymRepository;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GymDataImportService {
    private final KakaoAddressSearchService kakaoService;
    private final GymRepository gymRepository;

    public void importCsv(Path csvPath) {
        try (CSVReader reader = new CSVReaderBuilder(Files.newBufferedReader(csvPath, Charset.forName("CP949")))
                .withSkipLines(1) // 첫 줄 헤더 스킵
                .build()) {

            List<Gym> batchList = new ArrayList<>();
            String[] tokens;
            int count = 0;

            while ((tokens = reader.readNext()) != null) {
                String gymName = tokens[2].trim(); // 상호
                log.info("헬스장 상호명: " + gymName);
                String address = tokens[4].isBlank() ? tokens[3].trim() : tokens[4].trim(); // 도로명 없으면 지번 사용
                log.info("헬스장 주소: " + address);

                kakaoService.requestAddressSearch(address).ifPresent(res -> {
                    DocumentDto doc = res.getDocumentList().getFirst(); // 첫 번째 결과만 사용
                    Gym gym = Gym.builder()
                            .gymName(gymName)
                            .gymAddress(address)
                            .latitude(doc.getLatitude())
                            .longitude(doc.getLongitude())
                            .build();
                    log.info("헬스장 정보: " + gym);
                    log.info("====================================================");
                    batchList.add(gym);
                });

                count++;

                // 30건마다 저장 & 1초 대기 (Kakao API 속도 제한 대응)
                if (count % 30 == 0) {
                    gymRepository.saveAll(batchList);
                    log.info("{}건 저장 완료", batchList.size());
                    batchList.clear();
                    Thread.sleep(1000);
                }
            }

            // 남은 데이터 저장
            if (!batchList.isEmpty()) {
                gymRepository.saveAll(batchList);
                log.info("마지막 {}건 저장 완료", batchList.size());
            }

            // =====================
            // CSV 파일로 내보내기
            // =====================
            Path outputDir = Paths.get("src/main/resources/csv_output");
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            Path outputCsv = outputDir.resolve("gym_data.csv");

            try (CSVWriter writer = new CSVWriter(
                    Files.newBufferedWriter(outputCsv, StandardCharsets.UTF_8),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END)) {

                // 헤더 작성
                writer.writeNext(new String[]{"id", "createdDate","modifiedDate", "latitude", "longitude", "gymAddress", "gymName"});

                // DB에서 방금 저장한 데이터 불러오기
                List<Gym> gyms = gymRepository.findAll();
                for (Gym gym : gyms) {
                    writer.writeNext(new String[]{
                            gym.getId().toString(),
                            gym.getCreatedDate().toString(),
                            gym.getModifiedDate().toString(),
                            String.valueOf(gym.getLatitude()),
                            String.valueOf(gym.getLongitude()),
                            gym.getGymAddress(),
                            gym.getGymName()
                    });
                }
            }

            log.info("CSV 파일 저장 완료: {}", outputCsv);


        } catch (Exception e) {
            throw new RuntimeException("CSV 처리 중 오류 발생", e);
        }
    }
}
