# simple-ReAct-framework
ReAct Prompt Framework for java.
Reasoning Action架构的提示词工程

# 综述
参考了langchain的实现，使用了generate api，没用chat api
从这位大神这里拷贝了几个prompt模版，做了修改 https://smith.langchain.com/hub/hwchase17
实验上，如果你的模型不支持函数调用，或者不懂，那基本上就用不了，将request生成为LLAMA2-CHINESE它就完全看不懂这个格式，响应里会直接返回它不懂，并且没有按照prompt格式
总的来说，prompt工程还是基于模型开发的，即使是同1个prompt在不同模型上可能也有相差较远的表现，比较难通用，模型根据prompt做fine-tune似乎可以缓解
mistral:v3（langchain用的openai，我觉得openai应该也可以）是大部分能按照这种格式返回的

## 下一步工作
- lookup函数可以扩展RAG形式，如果模型认为他不知道，再进行检索
- 这个ollama4j可以替换，它的一些使用方式是错误的，当然也可能和ollama-server一直更新有关，有条件的可以重写（用spring boot3.x写的话很快）
  - 日志是ollama4j带的，在logback.xml里，把root改成INFO/DEBUG能看到模型请求/响应日志
- 一些令人生气的 corner case，有一些场景，LLM会吐出不按照格式的文本，导致程序产生异常，这个似乎只能case by case，否则只能让用户输入下一次文本
- 单页应用，通过web socket，也许可能用netty实现

# 运行
Java 17及以上 Maven 3.9

## IDE
直接定位 hao.simple.ai.cmd.App
点击按钮运行

## Jar
运行打包指令 mvn clean package
java -jar xxx-jar-with-dependencies.jar

# 示例
以mistral为为运行示例，每个问题是互不影响的，这个prompt能够比较好的处理: 是什么，怎么样，为什么

但如果你问它，how are you，可能就会出现意外，这应该取决于topk和topp的配置，太有想象力可能也不好

## 是什么（what）
可以看到预先准备的问题答案，它能够理解，但是不知道，它能够回答不知道
```
You: what is LangChain?
Thought: I should search for information about LangChain.
Action: search
Action Input: LangChain definition
Observation: LangChain is an open source orchestration framework for building applications using large language models (LLMs) like chatbots and virtual agents. It was launched by Harrison Chase in October 2022 and has gained popularity as the fastest-growing open source project on Github in June 2023.
Thought: I now know the final answer
Final Answer: LangChain is an open source orchestration framework for building applications using large language models (LLMs) like chatbots and virtual agents.
LangChain is an open source orchestration framework for building applications using large language models (LLMs) like chatbots and virtual agents.
You: Who is GOD?
Thought: I don't know who GOD is, but I might be able to find out by searching for it.
Action: search
Action Input: "Who is GOD"
Observation: Sorry, we don't find any relative information.
Thought: The search result doesn't contain the answer, let me try different search terms.
Action: search
Action Input: "GOD history and meaning"
Observation: Sorry, we don't find any relative information.
Thought: The search results still don't give an answer, maybe I can find something in different contexts or religious texts.
Action: search
Action Input: "GOD in religious texts"
Observation: Sorry, we don't find any relative information.
Thought: It seems that the information about GOD is not widely available on the internet. Let me try to summarize what I found from the limited results.
Final Answer: I am unable to provide a definitive answer as the search did not yield any conclusive results regarding who or what GOD is, although it may refer to a supreme being in religious contexts.
I am unable to provide a definitive answer as the search did not yield any conclusive results regarding who or what GOD is, although it may refer to a supreme being in religious contexts.
```
## 为什么（why）
它能够较好的理解问题，并且能够根据思路进行广泛的搜索，它也是基于当前的了解进行回答
```
You: why does the sun rise?
Thought: I don't know much about astronomy, so I should search for an answer.
Action: search
Action Input: Why does the sun rise?
Observation: Sorry, we don't find any relative information.
Thought: This might be a common question, let me try rephrasing it.
Action: search
Action Input: Why does the earth rotate and the sun appears to rise?
Observation: Sorry, we don't find any relative information.
Thought: It seems I need to specify that I'm looking for an explanation in terms of Earth's rotation. Let me try again.
Action: search
Action Input: Why does the earth rotate and the sun appears to rise? (simplified)
Observation: Sorry, we don't find any relative information.
Thought: Maybe I can get closer by breaking it down further.
Action: search
Action Input: What causes Earth to rotate and the sun appears to rise?
Observation: Sorry, we don't find any relative information.
Thought: This might be too complex for a simple search. Perhaps there are resources on popular science websites I can consult. Let me try that.
Action: search
Action Input: What causes Earth to rotate and the sun appears to rise? (popular science)
Observation: Sorry, we don't find any relative information.
Thought: This looks like a difficult question. Let me search for a more general term about astronomy.
Action: search
Action Input: How does Earth rotate and the sun rises daily?
Observation: Sorry, we don't find any relative information.
Thought: I need to try another approach. Maybe there are video resources that can help explain this. Let me search for videos.
Action: search
Action Input: Earth rotation and sunrise explained in a video (YouTube)
Observation: Sorry, we don't find any relative information.
Thought: It seems I need to specify the YouTube channel to limit my search. Let me try that.
Action: search
Action Input: Earth rotation and sunrise explained in a video (YouTube NASA)
Observation: Sorry, we don't find any relative information.
Thought: Maybe there are other educational channels that explain this topic. Let me search for those as well.
Action: search
Action Input: Earth rotation and sunrise explained in a video (YouTube space science)
Observation: Sorry, we don't find any relative information.
Thought: I need to give up for now, but I can remind myself to look it up later or consult an expert. For the sake of moving forward, let me provide an answer based on what I know so far.
Final Answer: The Earth rotates on its axis every 24 hours, and due to this rotation, the sun appears to rise in the east and set in the west. However, the exact mechanisms that cause Earth's rotation are complex and beyond my current knowledge.
The Earth rotates on its axis every 24 hours, and due to this rotation, the sun appears to rise in the east and set in the west. However, the exact mechanisms that cause Earth's rotation are complex and beyond my current knowledge.
```
## 怎么样（how to）
和上面的差不多，有时它能够基于当前知识回答
```
You: How to write a novel?
Thought: To find out how to write a novel, I should search for information about it.
Action: search
Action Input: "how to write a novel"
Observation: Sorry, we don't find any relative information.
Thought: It seems like the information provided is not detailed enough. Let me try again with a more specific query.
Action: search
Action Input: "step by step guide on how to write a novel"
Observation: Sorry, we don't find any relative information.
Thought: I should consult multiple resources to get a comprehensive answer.
Action: search
Action Input: "write a novel in 7 steps"
Observation: Sorry, we don't find any relative information.
Thought: It seems like the keyword or query is incorrect. Let me try again with a different approach.
Action: search
Action Input: "writing a novel from scratch"
Observation: Sorry, we don't find any relative information.
Thought: I should consult multiple resources to get a comprehensive answer.
Action: search
Action Input: "step by step guide on how to write a novel reddit"
Observation: Sorry, we don't find any relative information.
Thought: I should try to access more detailed resources like books or online classes.
Action: search
Action Input: "how to write a novel book"
Observation: Sorry, we don't find any relative information.
Thought: I should consult multiple resources to get a comprehensive answer.
Action: search
Action Input: "online course on how to write a novel"
Observation: Sorry, we don't find any relative information.
Thought: It seems like the information provided is not detailed enough or I am using the wrong keywords. Let me try again with a different approach.
Action: search
Action Input: "how to structure a novel"
Observation: Sorry, we don't find any relative information.
Thought: I should consult multiple resources to get a comprehensive answer.
Action: search
Action Input: "outline for writing a novel"
Observation: Sorry, we don't find any relative information.
Thought: It seems like the information provided is not detailed enough or I am using the wrong keywords. Let me try again with a different approach.
Action: search
Action Input: "novel writing tips and tricks"
Observation: Sorry, we don't find any relative information.
Thought: I should consult multiple resources to get a comprehensive answer.
Action: search
Action Input: "advice for writing a novel"
Observation: Sorry, we don't find any relative information.
Thought: It seems like the information provided is not detailed enough or I am using the wrong keywords. Let me try again with a different approach.
Action: search
Action Input: "how to write a novel step by step guide"
Observation: Sorry, we don't find any relative information.
Thought: It seems like there are no clear and concise resources on how to write a novel available online. Maybe I should consult experts or authors in the field for more specific advice.
Final Answer: It appears that there is limited information available online regarding how to write a novel step by step. You may want to consider consulting experts or authors in the field, reading books, or taking an online course on novel writing for more detailed guidance.
It appears that there is limited information available online regarding how to write a novel step by step. You may want to consider consulting experts or authors in the field, reading books, or taking an online course on novel writing for more detailed guidance.
```
