package com.sbidw.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.text.method.ScrollingMovementMethod;


public class ArticleContent extends Fragment {
    private static final String TAG = "ArticleContent";

    TextView headline;
    TextView date;
    TextView author;
    TextView content;
    ImageView photo;
    TextView ctr;
    Article article;
    int ctr1;
    View view;

    public static final String ARTICLE = "ARTICLE";
    public static final String INDEX = "INDEX";
    public static final String TOTAL = "TOTAL";

    public static final ArticleContent newInstance(Article article, int index, int total)
    {
        ArticleContent articleContent = new ArticleContent();
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(ARTICLE, article);
        bundle.putInt(INDEX, index);
        bundle.putInt(TOTAL, total);
        articleContent.setArguments(bundle);
        return articleContent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        article = (Article) getArguments().getSerializable(ARTICLE);
        ctr1 = getArguments().getInt(INDEX)+1;
        int total = getArguments().getInt(TOTAL);
        String endLine = ctr1 +" of "+total;


        view = inflater.inflate(R.layout.article_content, container, false);
        headline = (TextView) view.findViewById(R.id.headline);
        date = (TextView) view.findViewById(R.id.date);
        author = (TextView) view.findViewById(R.id.author);
        content = (TextView) view.findViewById(R.id.content);
        ctr = (TextView) view.findViewById(R.id.index);
        photo = (ImageView) view.findViewById(R.id.photo);

        ctr.setText(endLine);
        if(article.getTitle() != null){ headline.setText(article.getTitle());
        }
        else{headline.setText("");}

        if(article.getPublishedAt() !=null && !article.getPublishedAt().isEmpty()) {

            String dateofpublish = article.getPublishedAt();

            Date date1 = null;
            String pub_date = "";
            try {
                if(dateofpublish != null){

                    date1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateofpublish);}
                String s = "MMM dd, yyyy HH:mm";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(s);
                pub_date = simpleDateFormat.format(date1);
                date.setText(pub_date);
            } catch (ParseException e) {
            }
        }
        if(article.getDescription()!=null) {author.setText(article.getDescription());}
        else{author.setText("");}

        if(article.getAuthor() != null) {content.setText(article.getAuthor());}
        else{content.setText("");}

        author.setMovementMethod(new ScrollingMovementMethod());

        if(article.getUrlToImage()!=null){loadRemoteImage(article.getUrlToImage());}

        headline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(article.getUrl()));
                startActivity(intent);
            }
        });

        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(article.getUrl()));
                startActivity(intent);
            }
        });

        author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(article.getUrl()));
                startActivity(intent);
            }
        });


        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(article.getUrl()));
                startActivity(intent);
            }
        });

        return view;
    }


    private  void loadRemoteImage(final String imageURL){


        if (imageURL != null) {
            Picasso picasso = new Picasso.Builder(getActivity()).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    final String changedUrl = imageURL.replace("http:", "https:");
                    picasso.load(changedUrl)
                            .fit()
                            .centerCrop()
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(photo);
                }
            }).build();
            picasso.load(imageURL)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(photo);
        } else {
            Picasso.with(getActivity()).load(imageURL)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.missingimage)
                    .into(photo);
        }
    }
}
