package com.bajiezu.cloud.common.esign;

import com.alibaba.fastjson.JSONObject;
import com.timevale.esign.paas.sdk.bean.HttpConnectionConfig;
import com.timevale.esign.paas.sdk.bean.ProjectConfig;
import com.timevale.esign.paas.sdk.bean.SignatureConfig;
import com.timevale.esign.paas.sdk.constants.AlgorithmType;
import com.timevale.esign.paas.tech.bean.request.*;
import com.timevale.esign.paas.tech.bean.result.*;
import com.timevale.esign.paas.tech.client.ServiceClient;
import com.timevale.esign.paas.tech.client.ServiceClientManager;
import com.timevale.esign.paas.tech.enums.OrganizeTemplateType;
import com.timevale.esign.paas.tech.enums.SealColor;
import com.timevale.esign.paas.tech.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EsignService {
    /**
     * 应用ID
     */
    @Value("${esign.projectId}")
    private String projectId;
    /**
     * 应用密钥
     */
    @Value("${esign.projectSecret}")
    private String projectSecret;
    /**
     * e签宝 开发平台地址
     */
    @Value("${esign.apiUrl}")
    private String apiUrl;

    private AccountService acctService;
    private TemplateSealService templateSealService;
    private AuthService authService;
    private PdfDocumentService pdfDocumentService;
    private UserSignService userSignService;
    private PlatformSignService platformSignService;

    /**
     * 创建企业账号
     * 返回企业账号ID
     */
    public String addOrgAccount(OrganizeParam param) throws DefineException {
        if (acctService == null) {
            registerClient();
            acctService = getServiceClient().accountService();
        }
        AddAccountResult acctRst = acctService.addAccount(param);
        log.info("创建企业账号，acctRst:{}", JSONObject.toJSONString(acctRst));
        if (acctRst.getErrCode() != 0) {
            throw new DefineException(MessageFormat.format("创建企业账号失败：errCode = {0},msg = {1}",
                    acctRst.getErrCode(), acctRst.getMsg()));
        }
        log.info("创建企业账号成功:accountId={},请妥善保管AccountId以便后续签署场景使用", acctRst.getAccountId());
        return acctRst.getAccountId();
    }

    /**
     * 创建企业印章
     * 返回企业印章图片数据
     */
    public String createOfficialSeal(String accountId) throws DefineException {
        if (templateSealService == null) {
            registerClient();
            templateSealService = getServiceClient().templateSealService();
        }
        OrganizeTemplateType type = OrganizeTemplateType.STAR; // 印章模板类型,可选：STAR-标准公章 | DEDICATED-圆形无五角星章 | OVAL-椭圆形印章
        SealColor color = SealColor.RED;    // 印章颜色：RED-红色 | BLUE-蓝色 | BLACK-黑色
        String roundText = "";  // 印章里的企业名称（生成印章中的上弦文）,与accountId必须二选一传值
        List<String> hTexts = List.of("合同专用章");// hText 生成印章中的横向文内容 如“合同专用章、财务专用章”
        String qText = "";// qText 生成印章中的下弦文内容，公章防伪码（一般为13位数字），没有可以传空
        AddSealResult sealRst = templateSealService.createOrgSeal(accountId, type, roundText,
                hTexts, qText, color);
        log.info("创建企业模板印章, sealRst:{}", sealRst);
        if (sealRst.getErrCode() != 0) {
            throw new DefineException(MessageFormat.format("创建企业印章失败：errCode={0},msg={1}", sealRst.getErrCode(), sealRst.getMsg()));
        }
        log.info("创建企业模板印章成功：印章图片Base64数据sealData={}，可将该sealData保存到贵司数据库以便日后直接使用", sealRst.getSealData());
        return sealRst.getSealData();
    }

    /**
     * 【线上】发起企业授权书签署任务
     */
    public String createAuth(OnlineCreateAuthParam onlineCreateAuthParam) throws DefineException {
        if (authService == null) {
            registerClient();
            authService = getServiceClient().authService();
        }
        OnlineCreateAuthResult onlineCreateAuthResult = authService.createAuth(onlineCreateAuthParam);
        if (onlineCreateAuthResult.getErrCode() != 0) {
            log.info("发起企业授权失败：errCode = {},msg = {}",
                    onlineCreateAuthResult.getErrCode(), onlineCreateAuthResult.getMsg());
        } else {
            log.info("发起企业授权成功:authId={},请妥善保管authId以便后续签署场景关联", onlineCreateAuthResult.getAuthId());
            log.info("发起企业授权" + JSONObject.toJSONString(onlineCreateAuthResult));
        }
        return onlineCreateAuthResult.getAuthId();
    }

    /**
     * 填充PDF模板
     */
    public void fillTemplateByPdfFile(SignFilePdfParam file, Map<String, Object> txtFields) throws DefineException {
        if (pdfDocumentService == null) {
            registerClient();
            pdfDocumentService = getServiceClient().pdfDocumentService();
        }
        FileCreateFromTemplateResult rst = pdfDocumentService.createFileFromTemplate(file, true, txtFields);
        if (rst.getErrCode() != 0) {
            throw new DefineException(MessageFormat.format("本地PDF模板生成失败：errCode={0},errMsg={1}", rst.getErrCode(), rst.getMsg()));
        } else if (rst.getDstPdfFile() != null) {
            log.info("填充成功，填充后PDF文件保存路径：{}", rst.getDstPdfFile());
        }
    }

    /**
     * 企业签署
     */
    public void orgSignByPdfFile(OrgSignParam orgSignParam) throws DefineException {
        if (userSignService == null) {
            registerClient();
            userSignService = getServiceClient().userSignService();
        }
        FileDigestSignResult signRst = userSignService.orgSign(orgSignParam);
        log.info("签署结果：{}", signRst);
        if (signRst.getErrCode() != 0) {
            throw new DefineException(
                    MessageFormat.format("企业签署失败: errCode = {0},msg = {1}", signRst.getErrCode(), signRst.getMsg()));
        }
    }

    /**
     * 平台自身PDF文件签署
     */
    public void platformSignByPdfFile(PlatformSignParam platformSignParam) throws DefineException {
        FileDigestSignResult signRst = platformSignService.platformSign(platformSignParam);
        log.info("平台自身签署结果，signRst:{}", signRst);
        if (signRst.getErrCode() != 0) {
            throw new DefineException(
                    MessageFormat.format("平台自身签署失败: errCode = {0},msg = {1}", signRst.getErrCode(), signRst.getMsg()));
        }
    }

    private ServiceClient getServiceClient() throws DefineException {
        ServiceClient serviceClient = ServiceClientManager.get(projectId);
        if (serviceClient == null) {
            throw new DefineException(MessageFormat.format(
                    "ServiceClient为空，获取[{0}]的客户端失败，请重新注册客户端", projectId));
        }

        log.info("获取[{}]的客户端成功", projectId);
        return serviceClient;
    }

    public void registerClient() throws DefineException {
        // 1、进行项目配置，从开放平台获取
        ProjectConfig proCfg = getProjectCfg();
        // 2、Http配置
        HttpConnectionConfig httpConCfg = getHttpConCfg();
        // 3、签名配置
        SignatureConfig signCfg = getSignatureCfg();
        // 4、注册客户端
        Result rst = ServiceClientManager.registClient(proCfg, httpConCfg, signCfg);
        if (rst.getErrCode() != 0) {
            String rstMsg = MessageFormat.format("注册[{0}]的客户端失败：errorCode={1}，msg={2}",
                    projectId, rst.getErrCode(), rst.getMsg());
            throw new DefineException(rstMsg);
        }
        log.info("注册[{}]的客户端成功", projectId);
    }

    private ProjectConfig getProjectCfg() {
        ProjectConfig proCfg = new ProjectConfig();
        //项目ID（应用ID）
        proCfg.setProjectId(projectId);
        //项目Secret(应用Secret)
        proCfg.setProjectSecret(projectSecret);
        //开放平台地址
        proCfg.setItsmApiUrl(apiUrl);
        return proCfg;
    }

    private HttpConnectionConfig getHttpConCfg() {
        return new HttpConnectionConfig();
    }

    private SignatureConfig getSignatureCfg() {
        SignatureConfig signCfg = new SignatureConfig();
        signCfg.setAlgorithm(AlgorithmType.HMACSHA256);
        return signCfg;
    }
}
