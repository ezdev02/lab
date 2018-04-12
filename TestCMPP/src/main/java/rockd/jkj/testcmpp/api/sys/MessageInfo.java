package rockd.jkj.testcmpp.api.sys;

public interface MessageInfo
{

	public static final int CMPP_TOTAL_LENGTH_LEN = 4;

	public static final int CMPP_COMMAND_LEN = 4;

	public static final int CMPP_SEQUENCE_LEN = 4;

	public static final int CMPP_HEADER_LEN = CMPP_TOTAL_LENGTH_LEN + CMPP_COMMAND_LEN + CMPP_SEQUENCE_LEN;

}
