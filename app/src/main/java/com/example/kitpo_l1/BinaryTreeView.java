package com.example.kitpo_l1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class BinaryTreeView extends View {

    private BinaryTree tree;

    private Paint linePaint;
    private Paint nodePaint;
    private Paint textPaint;

    // базовые параметры (до масштабирования)
    private float baseLevelHeight = 140f;   // расстояние между уровнями
    private float baseNodeRadius  = 40f;    // радиус узла

    public BinaryTreeView(Context context) {
        super(context);
        init();
    }

    public BinaryTreeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BinaryTreeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(0xFF000000);
        linePaint.setStrokeWidth(4f);

        nodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        nodePaint.setColor(0xFF66BB6A);  // зелёные кружочки

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(0xFFFFFFFF);
        textPaint.setTextSize(32f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setTree(BinaryTree tree) {
        this.tree = tree;
        invalidate();  // перерисовать
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (tree == null) return;

        Node root = tree.getRoot();
        if (root == null) return;

        int heightLevels = getHeightLevels(root); // высота дерева
        if (heightLevels <= 0) return;

        float viewW = getWidth();
        float viewH = getHeight();

        // Сколько по идее нужно высоты без масштабирования
        float neededHeight = baseNodeRadius * 2f + baseLevelHeight * (heightLevels - 1) + 40f;

        // Коэффициент масштабирования по вертикали
        float scaleY = 1f;
        if (neededHeight > 0 && viewH > 0) {
            scaleY = viewH / neededHeight;
        }

        // Не увеличиваем, только уменьшаем (если scaleY < 1)
        float scale = Math.min(1f, scaleY);

        // Центр масштабирования: по X — центр view, по Y — верх дерева
        float cx = viewW / 2f;
        float cy = baseNodeRadius + 20f;

        canvas.save();
        canvas.scale(scale, scale, cx, 0f);

        // теперь рисуем дерево в "логических" координатах
        float startX = viewW / 2f;
        float startY = baseNodeRadius + 20f;
        float initialDx = viewW / 4f;   // базовое горизонтальное смещение

        drawNode(canvas, root, startX, startY, initialDx);

        canvas.restore();
    }

    private void drawNode(Canvas canvas, Node node, float x, float y, float dx) {
        if (node == null) return;

        float childY = y + baseLevelHeight;

        // Левый ребёнок
        if (node.left != null) {
            float leftX = x - dx;
            canvas.drawLine(x, y, leftX, childY, linePaint);
            drawNode(canvas, node.left, leftX, childY, dx / 2f);
        }

        // Правый ребёнок
        if (node.right != null) {
            float rightX = x + dx;
            canvas.drawLine(x, y, rightX, childY, linePaint);
            drawNode(canvas, node.right, rightX, childY, dx / 2f);
        }

        // Узел
        canvas.drawCircle(x, y, baseNodeRadius, nodePaint);

        String text = node.value == null ? "" : node.value.toString();
        canvas.drawText(text, x, y + (textPaint.getTextSize() / 3f), textPaint);
    }

    // высота дерева (кол-во уровней)
    private int getHeightLevels(Node n) {
        if (n == null) return 0;
        int lh = getHeightLevels(n.left);
        int rh = getHeightLevels(n.right);
        return 1 + Math.max(lh, rh);
    }
}
