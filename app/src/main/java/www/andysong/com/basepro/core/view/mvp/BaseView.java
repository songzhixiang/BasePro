package www.andysong.com.basepro.core.view.mvp;

/**
 * @author AndySong on 2019/1/2
 * @Blog https://github.com/songzhixiang
 */
public interface BaseView {

    void showErrorMsg(String msg);

    //=======  State  =======
    void stateError();

    void stateEmpty();

    void stateLoading();

    void stateMain();
}
