package bean;


/**
 * 数据类型
 *
 * @author grs
 */
public enum DataType {

    NOTYPE,
    NEWS,
    BBS,
    BLOG,
    WEIBO,
    VIDEO,
    ACADEMIC,
    EB,
    WEIXIN,
    EB_COMMENT,
    NO,
    REPORT,;


    public static DataType findType(String typeName) {
        for (DataType type : DataType.values()) {
            if (type.name().equalsIgnoreCase(typeName)) {
                return type;
            }
        }

        return null;
    }
}
