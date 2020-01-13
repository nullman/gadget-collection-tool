package com.nullman.gadget_collection_tool.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*
import javax.persistence.*

@Entity
@Table(
        name = "item",
        uniqueConstraints = [
            UniqueConstraint(
                name = "uidx__item__name",
                columnNames = [ "name" ]
            )
        ]
)
@JsonIgnoreProperties(ignoreUnknown = true)
class Item(
        id: UUID = UUID.randomUUID(),

        @Column(nullable = false)
        val name: String = "",

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(
                name = "bucket_id",
                foreignKey = ForeignKey(name = "fk__bucket__id")
        )
        val bucket: Bucket,

        @Column(nullable = false)
        val count: Int = 1
) : BaseEntity(id)
