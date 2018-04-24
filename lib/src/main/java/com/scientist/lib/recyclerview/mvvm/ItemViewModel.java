package com.scientist.lib.recyclerview.mvvm;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/4/24
 * Time: 10:24
 * Desc: MVVM结构的RecyclerView item
 */
public interface ItemViewModel {
    int getLayoutId();

    int getVariableId();

    ExtraViewModel[] getExtraViewModels();

    class ExtraViewModel {
        private int variableId;
        private Object viewModel;

        public ExtraViewModel(int variableId, Object viewModel) {
            this.variableId = variableId;
            this.viewModel = viewModel;
        }

        public int getVariableId() {
            return variableId;
        }

        public Object getViewModel() {
            return viewModel;
        }
    }
}
