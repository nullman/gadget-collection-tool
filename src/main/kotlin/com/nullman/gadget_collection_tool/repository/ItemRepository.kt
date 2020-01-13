package com.nullman.gadget_collection_tool.repository

import com.nullman.gadget_collection_tool.model.Item
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ItemRepository : CrudRepository<Item, UUID> {
    @Query(
            value = "SELECT * FROM item WHERE name = :name",
            nativeQuery = true
    )
    fun findByName(@Param("name") name: String): Optional<Item>

    @Query(
            value = "SELECT * FROM item WHERE bucket_id = :id",
            nativeQuery = true
    )
    fun findAllByBucketId(@Param("id") id: UUID): List<Item>
}
