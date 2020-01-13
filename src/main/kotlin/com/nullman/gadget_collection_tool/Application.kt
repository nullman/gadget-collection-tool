package com.nullman.gadget_collection_tool

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application {
}

fun main(args: Array<String>) {
    runApplication<Application>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
