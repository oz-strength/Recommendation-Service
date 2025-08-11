//package com.oz.project.touristSpot.service
//
//
//import com.oz.project.AbstractIntegrationContainerBaseTest
//import com.oz.project.touristSpot.repository.TouristSpotRepository
//import org.springframework.beans.factory.annotation.Autowired
//import spock.lang.Ignore
//
//import java.nio.file.Path
//import java.nio.file.Paths
//
//@Ignore("임시로 비활성화")
//class SpotDataImportServiceTest extends AbstractIntegrationContainerBaseTest {
//
//    @Autowired
//    GymDataImportService gymDataImportService
//
//    @Autowired
//    SpotRepository gymRepository
//
//    def "CSV 파일에서 헬스장 데이터 읽기"() {
//        given: "CSV 파일 경로 설정"
//        Path csvPath = Paths.get("src/main/resources/csv/gym.csv")
//
//        when: "CSV를 읽어들인다"
//        gymDataImportService.importCsv(csvPath)
//
//        then:
//        println("skip")
//    }
//}
