package com.nullman.gadget_collection_tool.integration

import com.nullman.gadget_collection_tool.helper.BucketHelper
import com.nullman.gadget_collection_tool.model.Bucket
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
class BucketIntegrationTest(@Autowired private val restTemplate: TestRestTemplate) {
    val bucketUrl = "/bucket"

    var bucket1: Bucket? = null
    var bucket2: Bucket? = null
    var bucket3: Bucket? = null
    val badBucket = Bucket(id = UUID(1000, 1), name = "Bad_Bucket_TEST")
    val newBucket = Bucket(id = UUID(1000, 2), name = "New_Bucket_TEST")
    val newBucketSameName = Bucket(id = UUID(1000, 3), name = "New_Bucket_TEST")
    val updateBucket = Bucket(id = UUID(1000, 2), name = "Update_Bucket_TEST")

    @BeforeAll
    fun beforeAll() {
        bucket1 = restTemplate.postForObject(bucketUrl, HttpEntity(BucketHelper.bucket1), Bucket::class.java)
        bucket2 = restTemplate.postForObject(bucketUrl, HttpEntity(BucketHelper.bucket2), Bucket::class.java)
        bucket3 = restTemplate.postForObject(bucketUrl, HttpEntity(BucketHelper.bucket3), Bucket::class.java)
    }

    @AfterAll
    fun afterAll() {
        restTemplate.delete("$bucketUrl/${bucket1!!.id}")
        restTemplate.delete("$bucketUrl/${bucket2!!.id}")
        restTemplate.delete("$bucketUrl/${bucket3!!.id}")
        restTemplate.delete("$bucketUrl/${badBucket.id}")
        restTemplate.delete("$bucketUrl/${newBucket.id}")
        restTemplate.delete("$bucketUrl/${newBucketSameName.id}")
    }

    @Test
    fun `findAll should return all bucket objects`() {
        val response = restTemplate.getForObject(bucketUrl, mutableListOf<LinkedHashMap<String, String>>()::class.java)
        assertEquals(3, response.size)
        assertEquals(bucket1!!.id.toString(), response[0]["id"])
        assertEquals(bucket1!!.name, response[0]["name"])
        assertEquals(bucket2!!.id.toString(), response[1]["id"])
        assertEquals(bucket2!!.name, response[1]["name"])
        assertEquals(bucket3!!.id.toString(), response[2]["id"])
        assertEquals(bucket3!!.name, response[2]["name"])
    }

    @Test
    fun `findById should return bucket object for given id`() {
        assertEquals(bucket1, restTemplate.getForObject("$bucketUrl/${bucket1!!.id}", Bucket::class.java))
        assertEquals(bucket2, restTemplate.getForObject("$bucketUrl/${bucket2!!.id}", Bucket::class.java))
        assertEquals(bucket3, restTemplate.getForObject("$bucketUrl/${bucket3!!.id}", Bucket::class.java))
    }

    @Test
    fun `findById should error if given id is not found`() {
        val response = restTemplate.getForEntity("$bucketUrl/${badBucket.id}", String::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertTrue(response.body!!.contains("\"message\":\"Bucket not found with id: ${badBucket.id}\""))
    }

    @Test
    fun `findByName should return bucket object for given name`() {
        assertEquals(bucket1, restTemplate.getForObject("$bucketUrl/query/${bucket1!!.name}", Bucket::class.java))
    }

    @Test
    fun `findByName should error if given name is not found`() {
        val response = restTemplate.getForEntity("$bucketUrl/query/${badBucket.name}", String::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertTrue(response.body!!.contains("\"message\":\"Bucket not found with name: ${badBucket.name}\""))
    }

    @Test
    fun `createBucket should create a new bucket`() {
        assertEquals(newBucket, restTemplate.postForObject(bucketUrl, newBucket, Bucket::class.java))
        assertEquals(newBucket, restTemplate.getForObject("$bucketUrl/${newBucket.id}", Bucket::class.java))
        restTemplate.delete("$bucketUrl/${newBucket.id}")
    }

    @Test
    fun `createBucket should error if name exists`() {
        assertEquals(newBucket, restTemplate.postForObject(bucketUrl, newBucket, Bucket::class.java))
        val response = restTemplate.postForEntity(bucketUrl, newBucketSameName, String::class.java)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertTrue(response.body!!.contains("\"message\":\"Bucket name exists: ${newBucketSameName.name}\""))
        restTemplate.delete("$bucketUrl/${newBucket.id}")
    }

    @Test
    fun `updateBucket should update existing bucket`() {
        restTemplate.postForObject(bucketUrl, newBucket, Bucket::class.java)
        assertEquals(newBucket, restTemplate.getForObject("$bucketUrl/${newBucket.id}", Bucket::class.java))
        restTemplate.put("$bucketUrl/${newBucket.id}", updateBucket)
        assertEquals(updateBucket, restTemplate.getForObject("$bucketUrl/${newBucket.id}", Bucket::class.java))
        restTemplate.delete("$bucketUrl/${newBucket.id}")
    }

    @Test
    fun `updateBucket should error if ids do not match`() {
        restTemplate.postForObject(bucketUrl, newBucket, Bucket::class.java)
        restTemplate.put("$bucketUrl/${badBucket.id}", updateBucket)
        restTemplate.delete("$bucketUrl/${newBucket.id}")
    }

    @Test
    fun `updateBucket should error if name exists in another record`() {
        val newBucket2 = Bucket(id = UUID(1000, 4), name = "New_Bucket2_TEST")
        val updateBucket2SameName = Bucket(id = UUID(1000, 4), name = "New_Bucket_TEST")
        restTemplate.postForObject(bucketUrl, newBucket, Bucket::class.java)
        restTemplate.postForObject(bucketUrl, newBucket2, Bucket::class.java)
        assertEquals(newBucket2, restTemplate.getForObject("$bucketUrl/${newBucket2.id}", Bucket::class.java))
        restTemplate.put("$bucketUrl/${updateBucket2SameName.id}", updateBucket2SameName)
        assertEquals(newBucket2, restTemplate.getForObject("$bucketUrl/${newBucket2.id}", Bucket::class.java))
        restTemplate.delete("$bucketUrl/${newBucket.id}")
        restTemplate.delete("$bucketUrl/${newBucket2.id}")
    }

    @Test
    fun `deleteBucket should delete bucket with given id`() {
        restTemplate.postForObject(bucketUrl, newBucket, Bucket::class.java)
        restTemplate.delete("$bucketUrl/${newBucket.id}")
        val response = restTemplate.getForEntity("$bucketUrl/${newBucket.id}", String::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertTrue(response.body!!.contains("\"message\":\"Bucket not found with id: ${newBucket.id}\""))
    }
}
