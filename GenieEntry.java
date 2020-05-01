/**
 * @Description 天猫精灵技能函数入口，FC handler：com.alibaba.ailabs.GenieEntry::handleRequest
 * @Version 1.0
 **/
public class GenieEntry extends AbstractEntry {
    private static final Map<String,String> knowledge = new HashMap<String, String>();

    static{
        knowledge.put("函数计算", "函数计算是事件驱动的全托管计算服务。使用函数计算，您无需采购与管理服务器等基础设施，只需编写并上传代码，并以事件驱动的方式触发函数执行，服务就可以平稳运行。");
        knowledge.put("云原生开发模式","云原生是一种构建和运行应用程序的方法，是一套技术体系和方法论,基于云原生构建应用简便快捷，部署应用轻松自如、运行应用按需伸缩。");
        knowledge.put("智能应用开放平台", "天猫精灵智能应用开放平台是原天猫精灵技能开放平台升级后的平台, 在原有技能的基础上增加了小程序、APK等应用的管理能力");
        knowledge.put("智能应用", "天猫精灵智能应用包括语音技能、支付宝小程序、APP等应用");

    }

    @Override
    public ResultModel<TaskResult> execute(TaskQuery taskQuery, Context context) {
        context.getLogger().info("taskQuery: " + JSON.toJSONString(taskQuery));
        //获取意图
        String intentName = taskQuery.getIntentName();
        //获取参数, 格式就是map<实体标识, 实体值>
        Map<String, String> paramMap = taskQuery.getSlotEntities().stream()
                .filter(slotItem -> slotItem.getLiveTime() == 0)
                .collect(Collectors.toMap(slotItem -> slotItem.getIntentParameterName(), slotItem -> slotItem.getStandardValue()));
        if("welcome".equals(intentName)){
            return successForReply("欢迎进入云开发答疑, 您可以按照如下格式向我提问哦【什么是云原生、函数计算是什么】");
        }else if("ask".equals(intentName)){
            String noun = paramMap.get("noun");
            if(StringUtils.isBlank(noun)){
                return successForAsk("我知道云原生、函数计算、智能应平台,你要了解那个", buildAsk(taskQuery.getIntentId()));
            }
            if(knowledge.containsKey(noun)){
                return successForReply(knowledge.get(noun));
            }else{
                return successForAsk("对不起, 您的问题超出了我的知识范围。您可以向我提问【什么是智能应用平台】。",buildAsk(taskQuery.getIntentId()));
            }
        }
        return successForAsk("对不起, 你的问题超出了我的知识范围。您可以向我提问【什么是智能应用平台，云计算是什么】。", buildAsk(taskQuery.getIntentId()));
    }

    /**
     *  追问模式
     * @param reply
     * @param askList
     * @return
     */
    private ResultModel<TaskResult> successForAsk(String reply, List<AskedInfoMsg> askList) {
        ResultModel<TaskResult> result = new ResultModel<>("0", "", "请求成功");
        TaskResult taskResult = new TaskResult();
        taskResult.setExecuteCode(ExecuteCode.SUCCESS);
        taskResult.setReply(reply);
        taskResult.setAskedInfos(askList);
        taskResult.setResultType(ResultType.ASK_INF);
        result.setReturnValue(taskResult);
        return result;
    }

    /**
     *  回复模式
     * @param reply
     * @return
     */
    private ResultModel<TaskResult> successForReply(String reply) {
        ResultModel<TaskResult> result = new ResultModel<>("0", "", "请求成功");
        TaskResult taskResult = new TaskResult();
        taskResult.setExecuteCode(ExecuteCode.SUCCESS);
        taskResult.setReply(reply);
        taskResult.setResultType(ResultType.RESULT);
        result.setReturnValue(taskResult);
        return result;
    }

    /**
     * 构建追问语料
     */
    private List<AskedInfoMsg> buildAsk(Long intentId){
        AskedInfoMsg asked = new AskedInfoMsg();
        asked.setIntentId(intentId);
        asked.setParameterName("noun");
        List<AskedInfoMsg> askedList = Lists.newArrayList(asked);
        return askedList;
    }
}
