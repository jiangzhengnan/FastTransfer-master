package com.guohui.fasttransfer.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.guohui.fasttransfer.R;
import com.guohui.fasttransfer.utils.FileSortFactory;
import com.guohui.fasttransfer.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by nangua on 2016/5/13.
 * 浏览本地所有文件的Adapter
 */
public class SendAtyFileFragAdapter extends BaseAdapter {

    Context context;
    ArrayList<File> files;

    public void addToFiles(File file){
        files.add(file);
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    /**
     * 构造方法
     *
     * @param context 上下文
     * @param files   文件集合
     */
    public SendAtyFileFragAdapter(Context context, ArrayList<File> files) {
        this.context = context;
        this.files = files;
        fileItemListener = new FileListItemListender();
        sort();
    }

    /**
     * MediaType
     * 0:图片
     * 1:音频
     * 2:视频
     */
    int MediaType = -1;
    /**
     * @param context
     * @param files
     */
    public SendAtyFileFragAdapter(Context context, ArrayList<File> files,int MediaType) {
        this.context = context;
        this.files = files;
        fileItemListener = new FileListItemListender();
        sort();
        this.MediaType = MediaType;
    }


    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public File getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //获取File
        File file = getItem(position);
        //声明holder
        ViewHolder holder = null;
        //如果没有可用的view
        if (convertView == null) {
            //加载view
            convertView = LayoutInflater.from(context).inflate(R.layout.sendaty_filefrag_rv_file_cell, null);
            //创建holder
            holder = new ViewHolder(convertView);
            //初始化所有控件

            //将holder与convertView进行绑定
            convertView.setTag(holder);

        } else {//如果存在view
            holder = (ViewHolder) convertView.getTag();
        }
        //设置文件名
        holder.tvFileName.setText(file.getName());
        //如果是图片或视频，则要获取缩略图显示
        if (MediaType == 2||MediaType==0) {
            //如果是图片
            if (MediaType == 0) {

                holder.ivFileImage.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
            } else {
                holder.ivFileImage.setImageBitmap(getVideoThumbnail(file.getPath(), 60, 60,MediaStore.Images.Thumbnails.MINI_KIND));
            }//
            //设置文件的大小//
            holder.tvFileSize.setText(FileUtils.generateSize(file));
        } else {
            if (file.isFile()) {
                //根据文件类型设置文件的图片
                holder.ivFileImage.setImageResource(FileUtils.filterFileTypeImage(file));
                //设置文件的大小
                holder.tvFileSize.setText(FileUtils.generateSize(file));
            }
            //如果file是一个文件夹
            else {
                //设置图片为文件夹
                holder.ivFileImage.setImageResource(R.drawable.folder);
                //设置大小为目录
                holder.tvFileSize.setText("目录");
            }
        }
        holder.tvFileTime.setText(FileUtils.generateTime(file));
        //将position与ibMore绑定
        holder.ibMore.setTag(position);
//        holder.ibMore.setOnClickListener(new FileListItemClickListener(holder.ibMore,position));
        //设置点击监听器
        holder.ibMore.setOnClickListener(fileItemListener);
        return convertView;

    }

/*    *//**
     * 根据指定的图像路径和大小来获取缩略图
     * 此方法有两点好处：
     *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     *        用这个工具生成的图像不会被拉伸。
     * @param imagePath 图像的路径
     * @param width 指定输出图像的宽度
     * @param height 指定输出图像的高度
     * @return 生成的缩略图
     */
    private Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    private Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                     int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }


    /**
     * ViewHolder
     */
    class ViewHolder {
        //文件名,文件大小,文件上次修改时间
        TextView tvFileName, tvFileSize, tvFileTime;
        //文件图片
        ImageView ivFileImage;
        //更多的功能
        ImageButton ibMore;

        ViewHolder(View v) {
            tvFileName = (TextView) v.findViewById(R.id.sendaty_filefrag_rv_file_cell_name);
            tvFileSize = (TextView) v.findViewById(R.id.sendaty_filefrag_rv_file_cell_size);
            tvFileTime = (TextView) v.findViewById(R.id.sendaty_filefrag_rv_file_cell_time);
            ivFileImage = (ImageView) v.findViewById(R.id.sendaty_filefrag_rv_file_cell_image);
            ibMore = (ImageButton) v.findViewById(R.id.sendaty_filefrag_rv_file_cell_more);
        }

    }

    public int getSortWay() {
        return sortWay;
    }

    public void setSortWay(int sortWay) {
        this.sortWay = sortWay;
    }

    //排序方法
    int sortWay = FileSortFactory.SORT_BY_FOLDER_AND_NAME;

    /**
     * 将文件列表排序
     */
    private void sort() {



        Collections.sort(this.files, FileSortFactory.getWebFileQueryMethod(sortWay));
    }

    @Override
    public void notifyDataSetChanged() {
        //重新排序
        sort();
        super.notifyDataSetChanged();
    }

    /**
     * ibButton点击监听器
     */
    FileListItemListender fileItemListener;


    /**
     * ibMore被点击的监听器
     * 点击的时候图标旋转并弹出menu,根据点击的view获取其绑定的position,之后再在file集合中操作数据
     */
    class FileListItemListender implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        Integer position;

        @Override
        public void onClick(final View v) {
            //获取view中绑定的tag
            position = (Integer) v.getTag();
            //创建菜单
            PopupMenu popupMenu = new PopupMenu(context, v);
            //加载布局
            popupMenu.inflate(R.menu.file_list_popup_menu);
            //设置消失时的监听器
            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu menu) {
                    //旋转动画(消失的时候会显示旋转动画)
                    RotateAnimation rotateAnimation = new RotateAnimation(90, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    //设置动画时间300ms
                    rotateAnimation.setDuration(300);
                    //设置动画保留状态
                    rotateAnimation.setFillAfter(true);
                    v.startAnimation(rotateAnimation);
                }
            });
            popupMenu.setOnMenuItemClickListener(this);
            RotateAnimation rotateAnimation = new RotateAnimation(0, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(300);
            rotateAnimation.setFillAfter(true);
            v.startAnimation(rotateAnimation);
            popupMenu.show();

        }

        /**
         * 菜单项点击
         *
         * @param item
         * @return true 事件处理完毕  false
         */
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_rename:
                    doRename();
                    break;
                case R.id.action_copy:
                    doCopy();
                    break;
                case R.id.action_remove:
                    doRemove();
                    break;
                default:
                    break;
            }
            return true;
        }

        /**
         * 删除
         */
        private void doRemove() {
            final File file = files.get(position);
            if (file.isFile()){
                FileUtils.judgeAlertDialog(context, "提醒", "你确认删除" + file.getName() + "吗?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        file.delete();
                        showToast("删除成功" + position);
                        notifyDataSetChanged();
                    }
                }, null);


            }else {
                showToast("不能删除文件夹" + position);
            }

        }

        /**
         * 显示消息
         *
         * @param message
         */
        private void showToast(String message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

        /**
         * 复制
         */
        private void doCopy() {
            showToast("复制" + position);
        }

        /**
         * 重命名
         */
        private void doRename() {
            showToast("重命名" + position);
//            String newName = null;
//            File file = files.get(position);
//
//            file.renameTo(new File(file.getAbsolutePath().replace(file.getName(),newName)));

        }

    }


}
