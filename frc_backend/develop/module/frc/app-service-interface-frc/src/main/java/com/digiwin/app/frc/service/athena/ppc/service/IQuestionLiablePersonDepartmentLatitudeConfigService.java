package com.digiwin.app.frc.service.athena.ppc.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
*@Author Jiangyw
*@Date 2022/3/14
*@Time 9:39
*@Version
*/
@DWRestfulService
public interface IQuestionLiablePersonDepartmentLatitudeConfigService extends DWService {

    @DWRequestMapping(path = "question/liable/person/department/latitude/config/info/create",method = {DWRequestMethod.POST})
    DWServiceResult addQuestionLiablePersonDepartmentLatitudeConfigInfo(String message) throws Exception;

    @DWRequestMapping(path = "question/liable/person/department/latitude/config/info/delete",method = {DWRequestMethod.POST})
    DWServiceResult deleteQuestionLiablePersonDepartmentLatitudeConfigInfo(String message) throws Exception;

    @DWRequestMapping(path = "question/liable/person/department/latitude/config/info/update",method = {DWRequestMethod.POST})
    DWServiceResult updateQuestionLiablePersonDepartmentLatitudeConfigInfo(String message) throws Exception;

    @DWRequestMapping(path = "question/liable/person/department/latitude/config/info/get",method = {DWRequestMethod.POST})
    DWServiceResult getQuestionLiablePersonDepartmentLatitudeConfigInfo(String message) throws Exception;

}
