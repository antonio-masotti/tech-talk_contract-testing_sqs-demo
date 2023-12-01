package com.bikeleasing.slack

import com.slack.api.Slack
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SlackService {

    private val webhook: String = System.getenv("SLACK_URL")
    private val slack: Slack = Slack.getInstance()
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Send a message to a slack channel
     *
     * @see <a href="https://slack.dev/java-slack-sdk/guides/incoming-webhooks">Slack API Docs</a>
     */
    fun sendMessage(message: String) {
        val resp = slack.send(webhook, getSlackPayload(message))
        logger.info("Sent message to slack: ${resp.message}")
    }

    private fun getSlackPayload(message: String): String {
        return Json.encodeToString(SlackPayload(message))
    }

}
