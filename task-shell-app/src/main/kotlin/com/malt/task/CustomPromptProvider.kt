package com.malt.task

import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle
import org.springframework.shell.jline.PromptProvider
import org.springframework.stereotype.Component

@Component
class CustomPromptProvider : PromptProvider {

    override fun getPrompt() = AttributedString(
            "taskman:> ",
            AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)
    )
}
