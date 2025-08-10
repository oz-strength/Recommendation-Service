package com.oz.project.gym.service

import com.opencsv.CSVReaderBuilder
import com.oz.project.AbstractIntegrationContainerBaseTest
import com.oz.project.api.dto.DocumentDto
import com.oz.project.gym.entity.Gym
import com.oz.project.gym.repository.GymRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Specification

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Ignore("임시로 비활성화")
class GymDataImportServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    GymDataImportService gymDataImportService

    @Autowired
    GymRepository gymRepository

    def "CSV 파일에서 헬스장 데이터 읽기"() {
        given: "CSV 파일 경로 설정"
        Path csvPath = Paths.get("src/main/resources/csv/gym.csv")

        when: "CSV를 읽어들인다"
        gymDataImportService.importCsv(csvPath)

        then:
        println("skip")
    }
}
