package com.furrowb.scheduler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SchedulerServiceApplication

fun main(args: Array<String>) {
	runApplication<SchedulerServiceApplication>(*args)
}
