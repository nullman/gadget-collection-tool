package com.nullman.gadget_collection_tool.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
@Table(
        name = "bucket",
        uniqueConstraints = [
            UniqueConstraint(
                    name = "uidx__bucket__name",
                    columnNames = [ "name" ]
            )
        ]
)
@JsonIgnoreProperties(ignoreUnknown = true)
class Bucket(
        id: UUID = UUID.randomUUID(),

        @Column(nullable = false)
        val name: String = ""
) : BaseEntity(id)
