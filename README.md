# simple-ReAct-framework
ReAct Framework for java.

这个prompt工程框架看上去在小模型7B情况下，提升效果没那么明显，要让模型明白Reason和Action模式就需要一定量的System Prompt
当前发现，需要用几个简单的问题引导小模型去理解框架，然后给予正向反馈，比如做得好，是这样的等等
经过以上带有记忆的对话，后续模型就能较好的应用框架。
但由于市面上的搜索引擎没有完全免费的，所以没有集成进来，可以使用RAG方式提取文档数据。
