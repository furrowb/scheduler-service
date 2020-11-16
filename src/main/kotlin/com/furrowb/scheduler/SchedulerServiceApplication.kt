package com.furrowb.scheduler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

// Need to create a basic test for this to have 100% test coverage
@SpringBootApplication
class SchedulerServiceApplication

fun main(args: Array<String>) {
	runApplication<SchedulerServiceApplication>(*args)
}
