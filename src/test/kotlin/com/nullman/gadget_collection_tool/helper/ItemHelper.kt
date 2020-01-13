package com.nullman.gadget_collection_tool.helper

import com.nullman.gadget_collection_tool.model.Item
import com.nullman.gadget_collection_tool.repository.ItemRepository
import java.util.*

class ItemHelper {
    companion object {
        val item1 = Item(id = UUID(2, 1), name = "Hammer_TEST", bucket = BucketHelper.bucket1, count = 1)
        val item2 = Item(id = UUID(2, 2), name = "Wrench_TEST", bucket = BucketHelper.bucket1, count = 2)
        val item3 = Item(id = UUID(2, 3), name = "Flathead_Screwdriver_TEST", bucket = BucketHelper.bucket1, count = 5)
        val item4 = Item(id = UUID(2, 4), name = "AA_TEST", bucket = BucketHelper.bucket2, count = 12)
        val item5 = Item(id = UUID(2, 5), name = "AAA_TEST", bucket = BucketHelper.bucket2, count = 6)
    }
}

