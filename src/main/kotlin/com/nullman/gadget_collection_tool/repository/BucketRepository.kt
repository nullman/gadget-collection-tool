package com.nullman.gadget_collection_tool.repository

import com.nullman.gadget_collection_tool.model.Bucket
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BucketRepository : CrudRepository<Bucket, UUID> {
    @Query(
            value = "SELECT * FROM bucket WHERE name = :name",
            nativeQuery = true
    )
    fun findByName(@Param("name") name: String): Optional<Bucket>
}
