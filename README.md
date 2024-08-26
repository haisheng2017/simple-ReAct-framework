# simple-ReAct-framework
ReAct Prompt Framework for java.
Reasoning Action架构的提示词工程

参考了langchain的实现，使用了generate api，没用chat api
从这位大神这里拷贝了几个prompt模版，做了修改 https://smith.langchain.com/hub/hwchase17
实验上，如果你的模型不支持函数调用，或者不懂，那基本上就用不了，将request生成为LLAMA2-CHINESE它就完全看不懂这个格式，响应里会直接返回它不懂，并且没有按照prompt格式
总的来说，prompt工程还是基于模型开发的，即使是同1个prompt在不同模型上可能也有相差较远的表现，比较难通用，模型根据prompt做fine-tune似乎可以缓解
mistral:v3（langchain用的openai，我觉得openai应该也可以）是肯定能按照这种格式返回的

lookup函数可以变成RAG形式，如果模型认为他不知道，再进行检索

这个ollama4j可以替换，它的一些使用方式是错误的，当然也可能和ollama-server一直更新有关，有条件的可以自己写（用spring boot3.x写的话很快）
日志是ollama4j带的，在logback.xml里，把root改成INFO/DEBUG都能看到