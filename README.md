# :pushpin: Recommendation-Service
>관광지 길찾기 서비스  
>http://13.125.59.117

</br>

## 1. 제작 기간 & 참여 인원
- 2025년 8월 07일 ~ 8월 15일
- 개인 프로젝트

</br>

## 2. 사용 기술
#### `Back-end`
  - Java 
  - Spring Boot 
  - Gradle
  - Spring Data JPA
  - MariaDB 
  - Thymeleaf
  - Docker

</br>

## 3. 핵심 기능
이 서비스의 핵심 기능은 근처 관광지 길찾기 기능입니다.  
사용자가 현재 위치한 장소를 검색하면 위치 기반 반경 10km 이내의 관광지를 3곳 추천해줍니다.

<details>
<summary><b>핵심 기능 설명 펼치기</b></summary>
<div markdown="1">

### 

- **사용자 입력주소를 통한 위도, 경도 변환** :pushpin: [코드 확인](https://github.com/oz-strength/Recommendation-Service/blob/f51b7150cc76e1a9bc9bb027b9c473205189a043/src/main/java/com/oz/project/api/service/KakaoAddressSearchService.java#L23)
  - RestTemplate을 통해 api를 호출하고 반환값을 별도의 DTO 클래스로 변환합니다.
  - 사용자가 현재 위치를 검색 시 카카오 `주소로 좌표 변환 api`를 통해 위도, 경도를 알아냅니다.
  - 빠른 응답을 위해, Redis에 캐싱된 값을 전달하고 그렇지 않다면 api호출 후 결과값을 json 형태로 캐싱합니다. (RedisConfig를 통한 Json변환설정)

- **위도, 경도, 반경을 통해 근처 관광지를 조회** :pushpin: [코드 확인](https://github.com/oz-strength/Recommendation-Service/blob/f51b7150cc76e1a9bc9bb027b9c473205189a043/src/main/java/com/oz/project/direction/service/DirectionService.java#L85)
  - Kakao `카테고리로 장소 검색` api를 통해 근처 10km내의 관광지 중 3곳을 반환합니다.
  - 빠른 응답을 위해, Redis에 캐싱된 값을 전달하고 그렇지 않다면 api호출 후 결과값을 json 형태로 캐싱합니다. (RedisConfig를 통한 Json변환설정)
  - 위도, 경도, 반경을 키값으로 하고 키값이 너무 세분화 되는 것을 막기 위해 위도, 경도를 각각 소수 4번째 자리까지 표기합니다.
  - (ChatGPT: 위도 1도는 약 111km, 서울중심 경도 1도는 약 89km, round(위도 또는 경도, 4) => 8~9m 단위 격자 효과)

- **길찾기 url 축약** :pushpin: [코드 확인](https://github.com/oz-strength/Recommendation-Service/blob/f51b7150cc76e1a9bc9bb027b9c473205189a043/src/main/java/com/oz/project/spot/service/SpotRecommendationService.java#L63)
  - 관광지의 id가 있는 `Direction` 엔티티의 id 값을 encoding 하여 사용자에게 접속한 ip(aws ec2 고정ip) 와 연결하여 보여줍니다. 
  - 사용자가 길찾기 url을 클릭하면 인코딩된 id를 디코딩 후 이를 통해 해당 `Direction` 엔티티를 찾아서 관광지 이름과 위도, 경도를 이용해 `카카오 길찾기 url`을 만들어 redirect 합니다.
    - [코드 확인](https://github.com/oz-strength/Recommendation-Service/blob/f51b7150cc76e1a9bc9bb027b9c473205189a043/src/main/java/com/oz/project/direction/controller/DirectionController.java#L13)

</div>
</details>

</br>

## 4. 핵심 트러블 슈팅
### 4.1. 도메인 데이터 부족으로 인한 도메인 변경 
- 초기에 공공기관 데이터를 활용해 Harversine Formula 거리계산 알고리즘을 이용하여 근처 헬스장을 추천하는 서비스를 만드려고 했습니다.

- 하지만 이미 kakao api에서 제공되는 기능이 있었을 뿐더러 헬스장 공공데이터를 제공하는 지역도 한정적이기 때문에 도메인을 변경하였습니다.

- kakao `카테고리로 장소 검색` api의 카테고리 중 관광명소로 도메인을 수정하였고 이전에 공공데이터를 db에 저장하고 활용하는 코드를 kakao rest api를 호출하는 코드로 수정하였습니다.

<details>
<summary><b>기존 코드</b></summary>
<div markdown="1">

~~~java
@Slf4j
@Service
@RequiredArgsConstructor
public class GymDataImportService {
    private final KakaoAddressSearchService kakaoService;
    private final TouristSpotRepository touristSpotRepository;

    public void importCsv(Path csvPath) {
        try (CSVReader reader = new CSVReaderBuilder(Files.newBufferedReader(csvPath, Charset.forName("CP949")))
                .withSkipLines(1) // 첫 줄 헤더 스킵
                .build()) {

            List<TouristSpot> batchList = new ArrayList<>();
            String[] tokens;
            int count = 0;

            while ((tokens = reader.readNext()) != null) {
                String gymName = tokens[2].trim(); // 상호
                log.info("헬스장 상호명: " + gymName);
                String address = tokens[4].isBlank() ? tokens[3].trim() : tokens[4].trim(); // 도로명 없으면 지번 사용
                log.info("헬스장 주소: " + address);

                kakaoService.requestAddressSearch(address).ifPresent(res -> {
                    DocumentDto doc = res.getDocumentList().getFirst(); // 첫 번째 결과만 사용
                    TouristSpot touristSpot = TouristSpot.builder()
                            .gymName(gymName)
                            .gymAddress(address)
                            .latitude(doc.getLatitude())
                            .longitude(doc.getLongitude())
                            .build();
                    log.info("헬스장 정보: " + touristSpot);
                    log.info("====================================================");
                    batchList.add(touristSpot);
                });

                count++;

                // 30건마다 저장 & 1초 대기 (Kakao API 속도 제한 대응)
                if (count % 30 == 0) {
                    touristSpotRepository.saveAll(batchList);
                    log.info("{}건 저장 완료", batchList.size());
                    batchList.clear();
                    Thread.sleep(1000);
                }
            }

            // 남은 데이터 저장
            if (!batchList.isEmpty()) {
                touristSpotRepository.saveAll(batchList);
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
                List<TouristSpot> touristSpots = touristSpotRepository.findAll();
                for (TouristSpot touristSpot : touristSpots) {
                    writer.writeNext(new String[]{
                            touristSpot.getId().toString(),
                            touristSpot.getCreatedDate().toString(),
                            touristSpot.getModifiedDate().toString(),
                            String.valueOf(touristSpot.getLatitude()),
                            String.valueOf(touristSpot.getLongitude()),
                            touristSpot.getTouristSpotAddress(),
                            touristSpot.getTouristSpotName()
                    });
                }
            }

            log.info("CSV 파일 저장 완료: {}", outputCsv);


        } catch (Exception e) {
            throw new RuntimeException("CSV 처리 중 오류 발생", e);
        }
    }
}
~~~

</div>
</details>

<details>
<summary><b>수정된 코드</b></summary>
<div markdown="1">

~~~java

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotRecommendationService {

    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final DirectionService directionService;
    private final Base62Service base62Service;

    @Value("${spot.recommendation.base.url}")
    private String baseUrl;

    private static final String ROAD_VIEW_BASE_URL = "https://map.kakao.com/link/roadview/";

    public List<OutputDto> recommendSpotList(String address) {

        return kakaoAddressSearchService.requestAddressSearch(address)
                .filter(response -> !isInvalidResponse(response))
                .map(response -> {
                    DocumentDto documentDto = response.getDocumentList().getFirst();
                    // 공공기관 데이터 및 거리계산 알고리즘 이용
                    // List<Direction> directionList = directionService.buildDirectionList(documentDto);

                    // kakao 카테고리를 이용한 장소 검색 api 이용
                    List<Direction> directionList = directionService.buildDirectionListByCategoryApi(documentDto);
                    return directionService.saveAll(directionList)
                            .stream()
                            .map(this::convertToOutputDto)
                            .toList();
                })
                .orElseGet(() -> {
                    log.error("[SpotRecommendationService][recommendSpotList] - fail >> Input address: {}", address);
                    return List.of();
                });
    }

    private boolean isInvalidResponse(KakaoApiResponseDto response) {
        return response == null
                || response.getDocumentList() == null
                || response.getDocumentList().isEmpty();
    }

    private OutputDto convertToOutputDto(Direction direction) {

        return OutputDto.builder()
                .spotName(direction.getTargetSpotName())
                .spotAddress(direction.getTargetAddress())
                .directionUrl(baseUrl + base62Service.encodeDirectionId(direction.getId())) // shorten url
                .roadViewUrl(ROAD_VIEW_BASE_URL + direction.getTargetLatitude() + "," + direction.getTargetLongitude())
                .distance(String.format("%.2f km", direction.getDistance()))
                .build();
    }
}

~~~

</div>
</details>

</br>

## 5. 그 외 트러블 슈팅
<details>
<summary>aws ec2 접속</summary>
<div markdown="1">

- skip-charset-client-handshake 설정 때문에 docker-compose 를 통한 빌드과정에서 mariadb가 안켜짐.
  - 위 설정을 통해 서버가 설정한 문자셋을 강제로 사용하려고 했지만 mariadb11 버전부터 charset handshake 과정이 개선되어 옵션을 켤 필요가 없어짐.

- 초기에 설치한 도커 컴포즈 버전이 오래되어 호환문제 발생.
  - 최신버전확인 후 최신 릴리즈 버전으로 다운로드
  - `sudo curl -L "https://github.com/docker/compose/releases/download/v2.39.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose`

- 환경변수 값을 집어넣지 않아서 springboot 실행 안되는 문제 발생.
  - vi .env 를 통해 환경변수 파일 생성

</div>
</details>

<details>
<summary>spring retry 테스트 코드 문제</summary>
<div markdown="1">
  
  - redis 적용 전에 spring retry 테스트 코드를 작성.
  - redis 를 통해 캐싱된 값 때문에 호출 횟수 차이가 발생하여 테스트 실패.
  - 레디스를 통해 캐싱된 값을 가져오는 부분을 Optional.empty() 를 반환하게끔 stubbing 하여 해결.
  - (https://github.com/oz-strength/Recommendation-Service/blob/f51b7150cc76e1a9bc9bb027b9c473205189a043/src/test/groovy/com/oz/project/api/service/KakaoAddressSearchServiceRetryTest.groovy#L33)
  
</div>
</details>
    
</br>

## 6. 회고 / 느낀점
>강의를 통해 학습한 짧은 프로젝트였지만 RestTemplate을 통해 api 호출하는 부분 및 Redis를 통해 값을 캐싱하여 api 호출을 줄이는 방법을 배울 수 있었다.
