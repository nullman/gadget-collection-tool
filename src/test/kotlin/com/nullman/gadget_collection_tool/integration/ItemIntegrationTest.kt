package com.nullman.gadget_collection_tool.integration

import com.nullman.gadget_collection_tool.helper.BucketHelper
import com.nullman.gadget_collection_tool.helper.ItemHelper
import com.nullman.gadget_collection_tool.model.Bucket
import com.nullman.gadget_collection_tool.model.Item
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment= WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemIntegrationTest(@Autowired private val restTemplate: TestRestTemplate) {
    val bucketUrl = "/bucket"
    val itemUrl = "/item"

    var bucket1: Bucket? = null
    var bucket2: Bucket? = null
    var bucket3: Bucket? = null
    final val badBucket = Bucket(id = UUID(1000, 1), name = "Bad_Bucket_TEST")
    final val newBucket = Bucket(id = UUID(1000, 2), name = "New_Bucket_TEST")
    final val updateBucket = Bucket(id = UUID(1000, 2), name = "Update_Bucket_TEST")

    var item1: Item? = null
    var item2: Item? = null
    var item3: Item? = null
    var item4: Item? = null
    var item5: Item? = null
    val badItem = Item(id = UUID(2000, 1), name = "Bad_Item_TEST", bucket = badBucket, count = 0)
    val newItem = Item(id = UUID(2000, 2), name = "New_Item_TEST", bucket = newBucket, count = 1)
    val newItemSameName = Item(id = UUID(2000, 3), name = "New_Item_TEST", bucket = newBucket, count = 1)
    val updateItem = Item(id = UUID(2000, 2), name = "Update_Item_TEST", bucket = updateBucket, count = 2)

    @BeforeAll
    fun beforeAll() {
        bucket1 = restTemplate.postForObject(bucketUrl, HttpEntity(BucketHelper.bucket1), Bucket::class.java)
        bucket2 = restTemplate.postForObject(bucketUrl, HttpEntity(BucketHelper.bucket2), Bucket::class.java)
        bucket3 = restTemplate.postForObject(bucketUrl, HttpEntity(BucketHelper.bucket3), Bucket::class.java)
        restTemplate.postForObject(bucketUrl, HttpEntity(newBucket), Bucket::class.java)
        item1 = restTemplate.postForObject(itemUrl, HttpEntity(ItemHelper.item1), Item::class.java)
        item2 = restTemplate.postForObject(itemUrl, HttpEntity(ItemHelper.item2), Item::class.java)
        item3 = restTemplate.postForObject(itemUrl, HttpEntity(ItemHelper.item3), Item::class.java)
        item4 = restTemplate.postForObject(itemUrl, HttpEntity(ItemHelper.item4), Item::class.java)
        item5 = restTemplate.postForObject(itemUrl, HttpEntity(ItemHelper.item5), Item::class.java)
    }

    @AfterAll
    fun afterAll() {
        restTemplate.delete("$itemUrl/${item1!!.id}")
        restTemplate.delete("$itemUrl/${item2!!.id}")
        restTemplate.delete("$itemUrl/${item3!!.id}")
        restTemplate.delete("$itemUrl/${item4!!.id}")
        restTemplate.delete("$itemUrl/${item5!!.id}")
        restTemplate.delete("$itemUrl/${badItem.id}")
        restTemplate.delete("$itemUrl/${newItem.id}")
        restTemplate.delete("$bucketUrl/${bucket1!!.id}")
        restTemplate.delete("$bucketUrl/${bucket2!!.id}")
        restTemplate.delete("$bucketUrl/${bucket3!!.id}")
        restTemplate.delete("$bucketUrl/${badBucket.id}")
        restTemplate.delete("$bucketUrl/${newBucket.id}")
    }

    @Test
    fun `findAll should return all item objects`() {
        val response = restTemplate.getForObject(itemUrl, mutableListOf<LinkedHashMap<String, String>>()::class.java)
        assertEquals(5, response.size)
        assertEquals(item1!!.id.toString(), response[0]["id"])
        assertEquals(item1!!.name, response[0]["name"])
        assertEquals(item1!!.count, response[0]["count"])
    }

    @Test
    fun `findById should return item object for given id`() {
        assertEquals(item1, restTemplate.getForObject("$itemUrl/${item1!!.id}", Item::class.java))
        assertEquals(item2, restTemplate.getForObject("$itemUrl/${item2!!.id}", Item::class.java))
        assertEquals(item3, restTemplate.getForObject("$itemUrl/${item3!!.id}", Item::class.java))
        assertEquals(item4, restTemplate.getForObject("$itemUrl/${item4!!.id}", Item::class.java))
        assertEquals(item5, restTemplate.getForObject("$itemUrl/${item5!!.id}", Item::class.java))
    }

    @Test
    fun `findById should error if given id is not found`() {
        val response = restTemplate.getForEntity("$itemUrl/${badItem.id}", String::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertTrue(response.body!!.contains("\"message\":\"Item not found with id: ${badItem.id}\""))
    }

    @Test
    fun `findByName should return item object for given name`() {
        assertEquals(item1, restTemplate.getForObject("$itemUrl/query/${item1!!.name}", Item::class.java))
    }

    @Test
    fun `findByName should error if given name is not found`() {
        val response = restTemplate.getForEntity("$itemUrl/query/${badItem.name}", String::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertTrue(response.body!!.contains("\"message\":\"Item not found with name: ${badItem.name}\""))
    }

    @Test
    fun `findAllByBucketId should return all item objects having given bucketId`() {
        val response = restTemplate.getForObject("$itemUrl/bucket/${bucket1!!.id}", mutableListOf<LinkedHashMap<String, String>>()::class.java)
        assertEquals(3, response.size)
        assertEquals(item1!!.id.toString(), response[0]["id"])
        assertEquals(item1!!.name, response[0]["name"])
        assertEquals(item1!!.count, response[0]["count"])
        assertEquals(item2!!.id.toString(), response[1]["id"])
        assertEquals(item2!!.name, response[1]["name"])
        assertEquals(item2!!.count, response[1]["count"])
        assertEquals(item3!!.id.toString(), response[2]["id"])
        assertEquals(item3!!.name, response[2]["name"])
        assertEquals(item3!!.count, response[2]["count"])
    }

    @Test
    fun `findAllByBucketId should return zero item objects for unused bucketId`() {
        val response = restTemplate.getForObject("$itemUrl/bucket/${bucket3!!.id}", mutableListOf<LinkedHashMap<String, String>>()::class.java)
        assertEquals(0, response.size)
    }

    @Test
    fun `createItem should create a new item`() {
        assertEquals(newItem, restTemplate.postForObject(itemUrl, newItem, Item::class.java))
        assertEquals(newItem, restTemplate.getForObject("$itemUrl/${newItem.id}", Item::class.java))
        restTemplate.delete("$itemUrl/${newItem.id}")
    }

    @Test
    fun `updateItem should update existing item`() {
        restTemplate.postForObject(itemUrl, newItem, Item::class.java)
        assertEquals(newItem, restTemplate.getForObject("$itemUrl/${newItem.id}", Item::class.java))
        restTemplate.put("$itemUrl/${newItem.id}", updateItem)
        assertEquals(updateItem, restTemplate.getForObject("$itemUrl/${newItem.id}", Item::class.java))
        restTemplate.delete("$itemUrl/${newItem.id}")
    }

    @Test
    fun `updateItem should error if ids do not match`() {
        restTemplate.postForObject(itemUrl, newItem, Item::class.java)
        restTemplate.put("$itemUrl/${badItem.id}", updateItem)
        restTemplate.delete("$itemUrl/${newItem.id}")
    }

    @Test
    fun `deleteItem should delete item with given id`() {
        restTemplate.postForObject(itemUrl, newItem, Item::class.java)
        restTemplate.delete("$itemUrl/${newItem.id}")
        val response = restTemplate.getForEntity("$itemUrl/${newItem.id}", String::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertTrue(response.body!!.contains("\"message\":\"Item not found with id: ${newItem.id}\""))
    }
}
