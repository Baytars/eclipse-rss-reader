package com.pnehrer.tools.morphine.ant;

import com.pnehrer.tools.morphine.*;

public class BindletCleanerAdaptor extends BindletToolAdaptor {

    public BindletCleanerAdaptor() {
        tool = new BindletCleaner();
    }
}
