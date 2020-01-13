package com.nullman.gadget_collection_tool.controller

import com.nullman.gadget_collection_tool.model.Item
import com.nullman.gadget_collection_tool.repository.ItemRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/item")
class ItemController(private val repository: ItemRepository) {
    @GetMapping
    fun findAll() = repository.findAll()

    @GetMapping("/{id}")
    fun findById(@PathVariable id: UUID) = repository.findById(id).orElseThrow {
        throw ItemNotFoundException("Item not found with id: $id")
    }

    @GetMapping("/query/{name}")
    fun findByName(@PathVariable name: String) = repository.findByName(name).orElseThrow {
        throw ItemNotFoundException("Item not found with name: $name")
    }

    @GetMapping("/bucket/{id}")
    fun findAllByBucketId(@PathVariable id: UUID) = repository.findAllByBucketId(id)

    @PostMapping()
    fun create(@RequestBody item: Item) =
            when {
                repository.findByName(item.name).isPresent ->
                    throw BucketNameExistsException("Bucket name exists: ${item.name}")
                else ->
                    repository.save(item)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody item: Item) =
            when {
                id != item.id ->
                    throw ItemIdNotMatchedException("Item URL id: $id does not match Bucket object id: ${item.id}")
                repository.findByName(item.name).orElse(item).id != id ->
                    throw ItemIdNotMatchedException("Item name exists: ${item.name}")
                else ->
                    repository.save(item)
            }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID) = repository.deleteById(id)
}

class ItemNotFoundException(message: String) : ResponseStatusException(HttpStatus.NOT_FOUND, message)

class ItemNameExistsException(message: String) : ResponseStatusException(HttpStatus.FORBIDDEN, message)

class ItemIdNotMatchedException(message: String) : ResponseStatusException(HttpStatus.FORBIDDEN, message)
