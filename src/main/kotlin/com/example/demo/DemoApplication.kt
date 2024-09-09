package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.jdbc.core.query
import org.springframework.web.bind.annotation.PathVariable
import java.sql.ResultSet
import java.util.*

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}

@RestController
class MessageController(val service: MessageService) {
	@GetMapping("/")
	fun findAll(): List<Message> = service.findMessages()

	@GetMapping("/{id}")
	fun findById(@PathVariable("id") id: String) = service.findMessageById(id)

	@PostMapping("/")
	fun save(@RequestBody message: Message) = service.save(message)
}

data class Message(val id: String?, val text: String)

@Service
class MessageService(val db: JdbcTemplate) {
	fun findMessages(): List<Message> = db.query("select * from messages", messageMapper)

	fun findMessageById(id: String): List<Message> = db.query("select * from messages where id = ?", id, function = messageMapper)

	val messageMapper = {response: ResultSet, _: Int ->
		Message(response.getString("id"), response.getString("text"))}

	fun save(message: Message) {
		val id = message.id ?: UUID.randomUUID().toString()
		db.update("insert into messages values (?, ?)", id, message.text)
	}
}
