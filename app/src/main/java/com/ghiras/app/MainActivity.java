package com.ghiras.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int GREEN = Color.rgb(13, 67, 51);
    private static final int GREEN_LIGHT = Color.rgb(35, 111, 79);
    private static final int GOLD = Color.rgb(218, 181, 93);
    private static final int CREAM = Color.rgb(248, 246, 239);
    private static final int INK = Color.rgb(35, 45, 39);

    private LinearLayout root, content, nav;
    private SharedPreferences prefs;
    private String currentTab = "home";
    private final String[] tasks = {
        "صلاة الفجر", "صلاة الظهر", "صلاة العصر", "صلاة المغرب", "صلاة العشاء",
        "قراءة الورد القرآني", "أذكار الصباح أو المساء"
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences("ghiras_local", MODE_PRIVATE);
        resetDailyIfNeeded();
        showApp();
    }

    private void resetDailyIfNeeded() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        if (!today.equals(prefs.getString("day", ""))) {
            prefs.edit().putString("day", today).apply();
        }
    }

    private void showApp() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(CREAM);
        setContentView(root);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(22), dp(20), dp(22), dp(18));
        header.setBackground(rounded(GREEN, 0, 0));
        TextView brand = text("غِراس  🌿", 27, GOLD, true);
        header.addView(brand);
        TextView subtitle = text("كل يوم طاعة، وكل طاعة غِراس", 14, Color.WHITE, false);
        subtitle.setPadding(0, dp(5), 0, 0);
        header.addView(subtitle);
        root.addView(header);

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(16), dp(16), dp(18));
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        nav = new LinearLayout(this);
        nav.setPadding(dp(5), dp(7), dp(5), dp(7));
        nav.setBackgroundColor(Color.WHITE);
        root.addView(nav);
        renderTab();
        renderNav();
    }

    private void renderNav() {
        nav.removeAllViews();
        addNavButton("الرئيسية", "home");
        addNavButton("التحديات", "tasks");
        addNavButton("القرآن", "quran");
        addNavButton("الصلاة", "prayer");
    }

    private void addNavButton(String label, String tab) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(12);
        b.setAllCaps(false);
        b.setTextColor(currentTab.equals(tab) ? GREEN_LIGHT : INK);
        b.setBackgroundColor(Color.TRANSPARENT);
        b.setOnClickListener(v -> { currentTab = tab; renderTab(); renderNav(); });
        nav.addView(b, new LinearLayout.LayoutParams(0, dp(48), 1));
    }

    private void renderTab() {
        content.removeAllViews();
        if ("home".equals(currentTab)) renderHome();
        else if ("tasks".equals(currentTab)) renderTasks();
        else if ("quran".equals(currentTab)) renderQuran();
        else renderPrayer();
    }

    private void renderHome() {
        String date = new SimpleDateFormat("EEEE، d MMMM", new Locale("ar")).format(new Date());
        addText(content, date, 14, GREEN_LIGHT, false, 0, 8);
        addText(content, "السلام عليك 👋", 24, INK, true, 0, 4);
        addText(content, "خطوة صغيرة اليوم تصنع عادة جميلة غدًا.", 14, Color.DKGRAY, false, 0, 16);

        int done = completedCount();
        int percent = (int)Math.round(done * 100.0 / tasks.length);
        LinearLayout treeCard = card();
        treeCard.addView(text("شجرة الطاعات", 19, GOLD, true));
        TextView tree = text(treeEmoji(done) + "\n" + treeStage(done), 23, Color.WHITE, true);
        tree.setGravity(Gravity.CENTER);
        tree.setPadding(0, dp(15), 0, dp(12));
        treeCard.addView(tree);
        treeCard.addView(text("إنجاز اليوم: " + done + " من " + tasks.length + "  •  " + percent + "%", 14, Color.WHITE, false));
        content.addView(treeCard);
        addText(content, "مهمتك اليوم", 20, INK, true, 0, 8);
        addText(content, "أكمل التحديات التي تناسب يومك، وسجّل إنجازك بنفسك.", 14, Color.DKGRAY, false, 0, 10);
        Button b = button("عرض تحديات اليوم");
        b.setOnClickListener(v -> { currentTab = "tasks"; renderTab(); renderNav(); });
        content.addView(b);
        addText(content, "تذكير لطيف: إذا فاتك شيء، ابدأ من جديد دون جلد للذات.", 13, GREEN_LIGHT, false, 0, 16);
    }

    private void renderTasks() {
        addText(content, "تحديات اليوم 🎯", 23, INK, true, 0, 6);
        addText(content, "ضع علامة عند إتمام المهمة. التقدم محفوظ على هذا الجهاز.", 13, Color.DKGRAY, false, 0, 12);
        for (int i = 0; i < tasks.length; i++) {
            final int index = i;
            boolean checked = prefs.getBoolean(key(i), false);
            LinearLayout row = new LinearLayout(this);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(14), dp(10), dp(12), dp(10));
            row.setBackground(rounded(Color.WHITE, 16, Color.rgb(230, 226, 213)));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.bottomMargin = dp(8);
            content.addView(row, lp);
            LinearLayout words = new LinearLayout(this);
            words.setOrientation(LinearLayout.VERTICAL);
            words.addView(text(tasks[i], 16, INK, true));
            words.addView(text(i < 5 ? "تسجيل إنجاز شخصي" : "عادة يومية اختيارية", 12, Color.GRAY, false));
            row.addView(words, new LinearLayout.LayoutParams(0, -2, 1));
            Button mark = button(checked ? "✓ تم" : "إتمام");
            mark.setTextSize(12);
            mark.setOnClickListener(v -> {
                boolean next = !prefs.getBoolean(key(index), false);
                prefs.edit().putBoolean(key(index), next).apply();
                renderTab();
            });
            row.addView(mark);
        }
        Button reset = button("إعادة ضبط تحديات اليوم");
        reset.setOnClickListener(v -> new AlertDialog.Builder(this)
            .setTitle("إعادة ضبط اليوم")
            .setMessage("هل تريد إلغاء علامات الإنجاز لهذا اليوم؟")
            .setNegativeButton("إلغاء", null)
            .setPositiveButton("إعادة الضبط", (d, w) -> {
                SharedPreferences.Editor e = prefs.edit();
                for (int i=0;i<tasks.length;i++) e.remove(key(i));
                e.apply(); renderTab();
            }).show());
        content.addView(reset);
    }

    private void renderQuran() {
        addText(content, "القرآن الكريم 📖", 23, INK, true, 0, 8);
        addText(content, "مكان هادئ لوردك اليومي", 14, GREEN_LIGHT, false, 0, 14);
        LinearLayout c = cardLight();
        c.addView(text("سورة الفاتحة", 19, GREEN, true));
        c.addView(text("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", 20, INK, true));
        c.addView(text("الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", 20, INK, false));
        c.addView(text("الرَّحْمَٰنِ الرَّحِيمِ", 20, INK, false));
        c.addView(text("مَالِكِ يَوْمِ الدِّينِ", 20, INK, false));
        c.addView(text("إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", 20, INK, false));
        c.addView(text("اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", 20, INK, false));
        c.addView(text("صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", 20, INK, false));
        content.addView(c);
        addText(content, "تنبيه مهم", 17, GREEN, true, 0, 6);
        addText(content, "هذه نسخة أولية للواجهة وتعرض الفاتحة فقط. لا تعتبر مكتبة القرآن كاملة بعد. قبل النشر يجب إدراج نص كامل من مصدر موثوق ومراجعته، ثم إضافة مشغل وتنزيلات صوتية مرخصة أو مسموح بها.", 14, Color.DKGRAY, false, 0, 12);
        Button audio = button("إضافة التلاوات الصوتية لاحقًا");
        audio.setOnClickListener(v -> new AlertDialog.Builder(this).setTitle("التلاوات الصوتية")
            .setMessage("ستضاف ميزة اختيار القارئ وتنزيل التلاوات للاستماع دون إنترنت في المرحلة التالية.").setPositiveButton("حسنًا", null).show());
        content.addView(audio);
    }

    private void renderPrayer() {
        addText(content, "مواقيت الصلاة 🕌", 23, INK, true, 0, 8);
        addText(content, "يجب ضبط المواقيت بحسب المدينة وطريقة الحساب المعتمدة محليًا.", 14, Color.DKGRAY, false, 0, 14);
        LinearLayout c = cardLight();
        String[] prayerNames = {"الفجر", "الشروق", "الظهر", "العصر", "المغرب", "العشاء"};
        String[] values = {"—", "—", "—", "—", "—", "—"};
        for (int i=0;i<prayerNames.length;i++) {
            LinearLayout line = new LinearLayout(this);
            line.setPadding(0, dp(9), 0, dp(9));
            line.addView(text(prayerNames[i], 17, INK, true), new LinearLayout.LayoutParams(0, -2, 1));
            line.addView(text(values[i], 17, GREEN_LIGHT, true));
            c.addView(line);
        }
        content.addView(c);
        addText(content, "لم تُفعّل حسابات المواقيت في هذه النسخة بعد؛ لا تعتمد على الشرطات أعلاه كمواقيت فعلية.", 13, Color.rgb(153, 76, 34), false, 0, 12);
        Button info = button("كيف ستعمل المواقيت؟");
        info.setOnClickListener(v -> new AlertDialog.Builder(this).setTitle("مواقيت دقيقة")
            .setMessage("سيتم دمج مكتبة حساب مواقيت موثوقة، مع اختيار المدينة وطريقة الحساب، ثم إضافة اتجاه القبلة والتنبيهات بعد الاختبار.").setPositiveButton("حسنًا", null).show());
        content.addView(info);
    }

    private String key(int i) { return "done_" + i; }
    private int completedCount() {
        int n=0; for (int i=0;i<tasks.length;i++) if (prefs.getBoolean(key(i), false)) n++; return n;
    }
    private String treeEmoji(int n) {
        if (n >= 7) return "🌳";
        if (n >= 5) return "🌿";
        if (n >= 3) return "🌱";
        if (n >= 1) return "🌰";
        return "🫘";
    }
    private String treeStage(int n) {
        if (n >= 7) return "الشجرة مزدهرة اليوم!";
        if (n >= 5) return "أغصانك تزداد اخضرارًا";
        if (n >= 3) return "بدأت البذرة تنمو";
        if (n >= 1) return "خطوتك الأولى بدأت";
        return "كل شجرة تبدأ ببذرة";
    }

    private LinearLayout card() {
        LinearLayout c = new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(dp(18), dp(18), dp(18), dp(18)); c.setBackground(rounded(GREEN, 20, GREEN));
        return c;
    }
    private LinearLayout cardLight() {
        LinearLayout c = new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(dp(16), dp(16), dp(16), dp(16)); c.setBackground(rounded(Color.WHITE, 18, Color.rgb(230,226,213)));
        return c;
    }
    private TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.RIGHT); t.setTextDirection(View.TEXT_DIRECTION_RTL);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }
    private void addText(LinearLayout parent, String s, int size, int color, boolean bold, int top, int bottom) {
        TextView t = text(s,size,color,bold); t.setPadding(dp(2),dp(top),dp(2),dp(bottom)); parent.addView(t);
    }
    private Button button(String s) {
        Button b = new Button(this); b.setText(s); b.setTextColor(Color.WHITE); b.setAllCaps(false);
        b.setBackground(rounded(GREEN_LIGHT, 14, GREEN_LIGHT)); return b;
    }
    private GradientDrawable rounded(int color, int radius, int stroke) {
        GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius));
        if (stroke != color) d.setStroke(dp(1), stroke); return d;
    }
    private int dp(float n) { return (int)(n * getResources().getDisplayMetrics().density + 0.5f); }
}