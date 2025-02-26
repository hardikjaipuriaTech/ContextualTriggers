package com.strath.ct.trigger

import com.strath.ct.contextdata.IContextDataHolder

/**
 * A simple trigger is one that requires a single data source to determine whether or not the user
 * should be prompted to perform new behaviour.
 *
 */
abstract class SimpleTrigger (holder: IContextDataHolder?) : ITrigger {

    override val complexity: Int = 1;
}