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
 * @version $Id: BindType.java,v 0.2 2007/05/15 13:45:29 
 */
public interface BindType
{

	/**
	 * <li>For CMPP: Messages sent from SP to ISMG.</li>
	 * <li>For SMPP: messages sent from the ESME (Transmitter) to the SMSC</li>
	 */
	public static byte BIND_TX = 0;

	/**
	 * <li>For CMPP: Messages sent from ISMG to SP.</li>
	 * <li>For SMPP: messages sent from the SMSC to the ESME (Receiver).</li>
	 */
	public static byte BIND_RX = 1;

	/**
	 * <li>For CMPP: Messages sent between ISMG and SP.</li>
	 * <li>For SMPP: messages sent from the ESME (Transceiver) to the SMSC and
	 * messages sent from the SMSC to the ESME (Transceiver).</li>
	 */
	public static byte BIND_TRX = 2;

}
