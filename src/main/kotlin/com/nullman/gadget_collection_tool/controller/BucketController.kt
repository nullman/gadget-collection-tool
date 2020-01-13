package com.nullman.gadget_collection_tool.controller

import com.nullman.gadget_collection_tool.model.Bucket
import com.nullman.gadget_collection_tool.repository.BucketRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/bucket")
class BucketController(private val repository: BucketRepository) {
    @GetMapping
    fun findAll() = repository.findAll()

    @GetMapping("/{id}")
    fun findById(@PathVariable id: UUID) = repository.findById(id).orElseThrow {
        throw BucketNotFoundException("Bucket not found with id: $id")
    }

    @GetMapping("/query/{name}")
    fun findByName(@PathVariable name: String) = repository.findByName(name).orElseThrow {
        throw BucketNotFoundException("Bucket not found with name: $name")
    }

    @PostMapping()
    fun create(@RequestBody bucket: Bucket) =
            when {
                repository.findByName(bucket.name).isPresent ->
                    throw BucketNameExistsException("Bucket name exists: ${bucket.name}")
                else ->
                    repository.save(bucket)
            }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody bucket: Bucket) =
            when {
                id != bucket.id ->
                    throw BucketIdNotMatchedException("Bucket URL id: $id does not match Bucket object id: ${bucket.id}")
                repository.findByName(bucket.name).orElse(bucket).id != id ->
                    throw BucketIdNotMatchedException("Bucket name exists: ${bucket.name}")
                else ->
                    repository.save(bucket)
            }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID) = repository.deleteById(id)
}

class BucketNotFoundException(message: String) : ResponseStatusException(HttpStatus.NOT_FOUND, message)

class BucketNameExistsException(message: String) : ResponseStatusException(HttpStatus.FORBIDDEN, message)

class BucketIdNotMatchedException(message: String) : ResponseStatusException(HttpStatus.FORBIDDEN, message)
