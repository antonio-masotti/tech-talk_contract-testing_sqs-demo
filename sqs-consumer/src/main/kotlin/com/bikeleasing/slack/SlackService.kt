package com.bikeleasing.slack

import com.slack.api.Slack
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class SlackService {
    private val logger = org.slf4j.LoggerFactory.getLogger(javaClass)
    private val webhook = System.getenv("SLACK_URL")
    private val slack = Slack.getInstance()

    /**
     * Send a message to a slack channel
     *
     * @see <a href="https://slack.dev/java-slack-sdk/guides/incoming-webhooks">Slack API Docs</a>
     */
    fun sendMessage(message: String) {

        val slackPayload = Json.encodeToString(SlackPayload(message))
        val resp = slack.send(webhook, slackPayload)
        logger.info("Sent message to slack: ${resp.code}")
    }


}
