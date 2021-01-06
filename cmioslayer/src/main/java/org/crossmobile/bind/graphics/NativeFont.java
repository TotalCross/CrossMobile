/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package org.crossmobile.bind.graphics;

import org.crossmobile.bridge.ann.CMLib;
import org.crossmobile.bridge.ann.CMLibTarget;

@CMLib(target = CMLibTarget.RUNTIME)
public interface NativeFont {

    String getName();

    String getFamily();

    double getSize();

    double getAscent();

    double getDescent();

    double getLeading();

    double getCapHeight();

    double getXHeight();
}
