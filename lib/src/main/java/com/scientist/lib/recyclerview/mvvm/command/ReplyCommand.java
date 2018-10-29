package com.scientist.lib.recyclerview.mvvm.command;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/10/15
 * Time: 23:54
 * Desc:
 */
public class ReplyCommand<T> {

    private Action0 execute0;
    private Action1<T> execute1;
    private Func0<Boolean> canExecute0;

    public ReplyCommand(Action0 execute) {
        this.execute0 = execute;
    }

    public ReplyCommand(Action1<T> execute) {
        this.execute1 = execute;
    }

    public ReplyCommand(Action0 execute, Func0<Boolean> canExecute) {
        this.execute0 = execute;
        this.canExecute0 = canExecute;
    }

    public ReplyCommand(Action1<T> execute, Func0<Boolean> canExecute) {
        this.execute1 = execute;
        this.canExecute0 = canExecute;
    }

    public void execute() {
        if (execute0 != null && canExecute()) {
            execute0.call();
        }
    }

    public void execute(T t) {
        if (execute1 != null && canExecute()) {
            execute1.call(t);
        }
    }

    public boolean canExecute() {
        if (canExecute0 == null)
            return true;
        return canExecute0.call();
    }
}
