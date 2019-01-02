package www.andysong.com.basepro.core.view.mvp;

/**
 * @author AndySong on 2019/1/2
 * @Blog https://github.com/songzhixiang
 */
public interface BasePresenter<T extends BaseView>{

    void attachView(T view);

    void detachView();
}
