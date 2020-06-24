/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package crossmobile.ios.foundation;

import org.crossmobile.bridge.ann.CMLib;
import org.crossmobile.bridge.ann.CMLibTarget;
import org.robovm.objc.block.Block0;

@CMLib(target = CMLibTarget.RUNTIME_PLUGIN)
public class FoundationDrill {
    public static void addVirtualUserDefault(String key, Block0<Object> supplier) {
        NSUserDefaults.standardUserDefaults().addVirtualValue(key, supplier);
    }

    public static void quitTimers() {
        NSRunLoop.mainRunLoop().invalidateAll();
    }

    public static boolean isUnderMainRunLoop() {
        return NSRunLoop.isUnderMainRunLoop();
    }
}