package io.github.seggan.notwalshbot.commands

import dev.kord.rest.builder.interaction.string
import io.github.seggan.notwalshbot.filters.ScamFilter
import io.github.seggan.notwalshbot.httpClient
import io.github.seggan.notwalshbot.server.Roles
import io.github.seggan.notwalshbot.util.respondPublic
import io.ktor.client.request.*
import io.ktor.http.*

object ReportCommand : CommandExecutor("report", "Report scam domain") {

    override val args: CommandBuilder = {
        string("domain", "The domain to report")
    }
    override val permission = Roles.moderator

    override suspend fun CommandEvent.execute() {
        httpClient.post("https://bad-domains.walshy.dev/report") {
            setBody(
                """
                {
                    "domain": "${interaction.command.strings["domain"]!!}"
                }
                """
            )
            contentType(ContentType.Application.Json)
        }
        interaction.respondPublic("Reported domain ${interaction.command.strings["domain"]!!}")
        ScamFilter.updateScamCache()
    }
}