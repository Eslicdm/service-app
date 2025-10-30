package com.eslirodrigues.member_request_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class MemberRequestServiceApplication

fun main(args: Array<String>) {
    runApplication<MemberRequestServiceApplication>(*args)
}