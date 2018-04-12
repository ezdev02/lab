/*
 * ============================================================================
 * GNU Lesser General Public License
 * ============================================================================
 *
 * deer-cmpp  - Free Java cmpp library.
 * http://deer-cmpp.sourceforge.net
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package rockd.jkj.testcmpp.api.sys;

/**
 * 
 * @author luomingjie (luomingjie@users.sourceforge.net ; luyiluo@126.com)
 * @version $Id: CommandID.java,v 0.2 2007/05/15 13:45:29 
 */
public interface CommandID
{

	public final static int CMPP_NACK = 0x00000000;

	public final static int CMPP_NACK_RESP = 0x80000000;

	public final static int CMPP_CONNECT = 0x00000001;

	public final static int CMPP_CONNECT_RESP = 0x80000001;

	public final static int CMPP_TERMINATE = 0x00000002;

	public final static int CMPP_TERMINATE_RESP = 0x80000002;

	public final static int CMPP_SUBMIT = 0x00000004;

	public final static int CMPP_SUBMIT_RESP = 0x80000004;

	public final static int CMPP_DELIVER = 0x00000005;

	public final static int CMPP_DELIVER_RESP = 0x80000005;

	public final static int CMPP_ACTVIE_TEST = 0x00000008;

	public final static int CMPP_ACTVIE_TEST_RESP = 0x80000008;

}
