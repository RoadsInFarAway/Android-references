package me.shouheng.references.view.guokr.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import javax.inject.Inject;

import me.shouheng.commons.util.ToastUtils;
import me.shouheng.commons.widget.DividerItemDecoration;
import me.shouheng.references.R;
import me.shouheng.references.databinding.FragmentNewsListBinding;
import me.shouheng.references.view.CommonDaggerFragment;
import me.shouheng.references.view.guokr.adapter.GuokrNewsAdapter;
import me.shouheng.references.viewmodel.GuokrViewModel;

/**
 * @author shouh
 * @version $Id: NewsListFragment, v 0.1 2018/6/10 12:08 shouh Exp$
 */
public class NewsListFragment extends CommonDaggerFragment<FragmentNewsListBinding> {

    @Inject GuokrViewModel guokrViewModel;

    private int offset = 0;

    private final int limit = 20;

    private GuokrNewsAdapter adapter;

    public static NewsListFragment newInstance() {
        Bundle args = new Bundle();
        NewsListFragment fragment = new NewsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_news_list;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        configViews();

        fetchNews();
    }

    private void configViews() {
        adapter = new GuokrNewsAdapter(getContext());
        adapter.setOnItemClickListener(((adapter1, view, position) -> {}));
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(() -> {
            offset += limit;
            fetchNews();
        }, getBinding().rv);

        getBinding().rv.setAdapter(adapter);
        getBinding().rv.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST, false));
        getBinding().rv.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void fetchNews() {
        guokrViewModel.getGuokrNews(offset, limit).observe(this, guokrNewsResource -> {
            if (guokrNewsResource == null) {
                return;
            }
            switch (guokrNewsResource.status) {
                case FAILED:
                    ToastUtils.makeToast(guokrNewsResource.message);
                    break;
                case SUCCESS:
                    adapter.addData(guokrNewsResource.data.getResult());
                    adapter.notifyDataSetChanged();
                    break;
            }
        });
    }
}