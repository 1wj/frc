package com.digiwin.app.frc.service.athena.common.Const;

/**
 * @author zcj
 * @description
 * @date 2021/10/4 9:16
 */
public class SystemConstant {
    public static final String ENABLE_STATUS="true";

    public static final String TRANSFER_ENABLE_STATUS="false";

    public static final String INDEX_FLAG=".";

    public static final String DEFAULT_VALUE_ONE="1";

    public static final String DEFAULT_VALUE_ZERO="0";

    public static final String DEFAULT_VALUE_MINUS_ONE="-1";

    public static final String DEFAULT_VALUE_TWO="2";

    public static final String DEFAULT_VALUE_NULL="null";

    public static final int DEFAULT_VALUE_INT_TWO=2;

    public static final int DEFAULT_VALUE_INT_FIVE=50000;

    public static final String TIME_HOUR="hour";

    public static final String TIME_DAY="day";

    public static final String TIME_MOUTH="month";

    public static final int TIME_ONE_MOUTH=30;

    /**
     * request/response params constant
     */
    public static final String MESSAGE_BODY="messageBody";
    public static final String MESSAGE_BODY_STD_DATA="std_data";
    public static final String MESSAGE_BODY_EXECUTION_CODE="code";
    public static final String MESSAGE_BODY_EXECUTION_CODE_NUM="0";

    public static final String REQUEST_PARAMS_IS_FIRST_VER="isFirstVersion";

    public static final String REQUEST_PARAMS_IS_FIRST_VER_Y="Y";
    public static final String REQUEST_PARAMS_BEAN_BRAND="brand";
    public static final String REQUEST_PARAMS_BEAN_STATUS="status";

    public static final String RESPONSE_BODY_EXECUTE="executeCode";

    public static final String RESPONSE_BODY_CAD_3D="cad3DUploads";

    public static final String QUEUE_PARAMS_ERROR="error";



    /**
     * cad file type constant
     */

    public static final String FILE_JPEG="jpeg";

    public static final String FILE_MSIE="MSIE";

    public static final String FILE_TRIDENT="Trident";

    public static final String FILE_BMP="bmp";

    public static final String FILE_GIF="gif";

    public static final String FILE_JPG="jpg";

    public static final String FILE_PNG="png";

    public static final String FILE_HTML="html";

    public static final String FILE_TXT="txt";

    public static final String FILE_XLS="xls";

    public static final String FILE_XLSX="xlsx";

    public static final String FILE_PDF="pdf";

    public static final String FILE_VSD="vsd";

    public static final String FILE_PPTX="pptx";

    public static final String FILE_PPT="ppt";

    public static final String FILE_DOCX="docx";

    public static final String FILE_DOC="doc";

    public static final String FILE_XML="xml";

    public static final String FILE_CSS="css";

    public static final String FILE_CLASS="class";

    public static final String FILE_MP4="mp4";

    public static final String FILE_SVG="svg";

    public static final int FILE_BUFFER_LENGTH=1024;

    public static final int FILE_PART_COUNT=10000;

    /**
     * properties constant
     */
    public static final String FILE_PATH="{dwExcelName}";

    public static final String FILE_NAME="{name}";

    /**
     * ftpClient about constant
     */
    public static final String COMMAND_CHART="OPTS UTF8";

    public static final String COMMAND_CHART_ON="ON";

    /**
     * other constant
     */

    public final static String FLAG_CHECK="check";

    /**
     *再次取图-图纸状态
     */
    /**
     * 取图完成
     */
    public static final int CAD_FILE_SAVE_COMPLETE=1;

    /**
     * 等待取图
     */
    public static final int CAD_FILE_SAVE_WAIT=0;

    /**
     * 取图超时，图纸失效
     */
    public static final int CAD_FILE_SAVE_INVALID=2;
}
