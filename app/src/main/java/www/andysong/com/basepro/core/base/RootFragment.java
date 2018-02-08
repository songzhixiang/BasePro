package www.andysong.com.basepro.core.base;

import android.view.View;
import android.view.ViewGroup;

import www.andysong.com.basepro.R;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/02/07
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public abstract class RootFragment extends BaseFragment {

    private View viewError;
    private View viewLoading;
    private View viewEmpty;
    private ViewGroup viewMain;
    private ViewGroup mParent;

    private static final int STATE_MAIN = 0x00;
    private static final int STATE_LOADING = 0x01;
    private static final int STATE_ERROR = 0x02;
    private static final int STATE_EMPTY = 0x03;

    private int mErrorResource = R.layout.view_loading_error;

    private int mEmptyResource = R.layout.view_loading_empty;

    private int currentState = STATE_MAIN;

    private boolean isErrorViewAdded = false;

    private boolean isEmptyViewAdded = false;

    protected void initEventAndData(View mView) {
        if (getView() == null)
            return;
        viewMain = getView().findViewById(R.id.recylcerview);
        if (viewMain == null) {
            throw new IllegalStateException(
                    "The subclass of RootActivity must contain a View named 'view_main'.");
        }
        if (!(viewMain.getParent() instanceof ViewGroup)) {
            throw new IllegalStateException(
                    "view_main's ParentView should be a ViewGroup.");
        }
        mParent = (ViewGroup) viewMain.getParent();
        View.inflate(mActivity, R.layout.view_loading, mParent);
        viewLoading = mParent.findViewById(R.id.fragment_loading);
        viewLoading.setVisibility(View.GONE);
        viewMain.setVisibility(View.VISIBLE);
    }


    /**
     * 显示错误视图
     */
    public void stateError() {
        if (currentState == STATE_ERROR)
            return;
        if (!isErrorViewAdded) {
            isErrorViewAdded = true;
            View.inflate(mActivity, mErrorResource, mParent);
            viewError = mParent.findViewById(R.id.globalError);
            if (viewError == null) {
                throw new IllegalStateException(
                        "A View should be named 'view_error' in ErrorLayoutResource.");
            }
        }
        hideCurrentView();
        currentState = STATE_ERROR;
        viewError.setVisibility(View.VISIBLE);
    }

    /**
     * 显示空视图
     */
    public void stateEmpty() {
        if (currentState == STATE_EMPTY)
            return;
        if (!isEmptyViewAdded) {
            isEmptyViewAdded = true;
            View.inflate(mActivity, mEmptyResource, mParent);
            viewEmpty = mParent.findViewById(R.id.globalEmpty);
            if (viewEmpty == null) {
                throw new IllegalStateException(
                        "A View should be named 'view_error' in ErrorLayoutResource.");
            }
        }
        hideCurrentView();
        currentState = STATE_EMPTY;
        viewEmpty.setVisibility(View.VISIBLE);

    }


    /**
     * 显示加载视图
     */
    public void stateLoading() {
        if (currentState == STATE_LOADING)
            return;
        hideCurrentView();
        currentState = STATE_LOADING;
        viewLoading.setVisibility(View.VISIBLE);

    }


    /**
     * 显示内容
     */
    public void stateMain() {
        if (currentState == STATE_MAIN)
            return;
        hideCurrentView();
        currentState = STATE_MAIN;
        viewMain.setVisibility(View.VISIBLE);
    }

    private void hideCurrentView() {
        switch (currentState) {
            case STATE_MAIN:
                viewMain.setVisibility(View.GONE);
                break;
            case STATE_LOADING:
                viewLoading.setVisibility(View.GONE);
                break;
            case STATE_ERROR:
                if (viewError != null) {
                    viewError.setVisibility(View.GONE);
                }
                break;
        }
    }

    public void setErrorResource(int errorLayoutResource) {
        this.mErrorResource = errorLayoutResource;
    }
}
