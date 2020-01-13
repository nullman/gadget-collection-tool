package com.nullman.gadget_collection_tool.helper

import com.nullman.gadget_collection_tool.model.Bucket
import com.nullman.gadget_collection_tool.repository.BucketRepository
import java.util.*

open class BucketHelper {
    companion object {
        val bucket1 = Bucket(id = UUID(1, 1), name = "Tool_TEST")
        val bucket2 = Bucket(id = UUID(1, 2), name = "Battery_TEST")
        val bucket3 = Bucket(id = UUID(1, 3), name = "PC_Component_TEST")
    }
}
