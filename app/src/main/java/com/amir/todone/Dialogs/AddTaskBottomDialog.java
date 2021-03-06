package com.amir.todone.Dialogs;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.amir.todone.Domain.Category.Category;
import com.amir.todone.Domain.Category.CategoryManager;
import com.amir.todone.Domain.Task.SubTask;
import com.amir.todone.Domain.Task.Task;
import com.amir.todone.Domain.Task.TaskManager;
import com.amir.todone.Objects.DateManager;
import com.amir.todone.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddTaskBottomDialog extends BottomSheetDialogFragment {

    public interface BottomSheetDialogListener {
        void onDismiss();
    }

    private LinearLayout subList, addASubtaskOption;

    private View extraSpace;
    private Button btnAdd;
    private TextView txtCategorySelected, txtDateSelected, txtTimeSelected;
    private LinearLayout bottomSheetContent;
    private EditText edtTaskText;
    private ConstraintLayout categoryOp, dateOp, timeOp;
    private BottomSheetBehavior bottomSheetBehavior;

    private Category taskCategory = null;
    private String taskDate = null;
    private String taskTime = null;


    private BottomSheetDialogListener listener;

    public static AddTaskBottomDialog newInstance() {
        return new AddTaskBottomDialog();
    }

    public void setListener(BottomSheetDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.new_task_bottomsheet_layout, null);
        extraSpace = view.findViewById(R.id.extraSpace);
        btnAdd = view.findViewById(R.id.btnAdd);
        bottomSheetContent = view.findViewById(R.id.bottomSheetContent);
        subList = view.findViewById(R.id.subList);
        addASubtaskOption = view.findViewById(R.id.addASubtaskOption);
        edtTaskText = view.findViewById(R.id.edtTaskText);
        txtCategorySelected = view.findViewById(R.id.txtSelectedCategory);
        txtDateSelected = view.findViewById(R.id.txtSelectedDate);
        txtTimeSelected = view.findViewById(R.id.txtSelectedTime);
        categoryOp = view.findViewById(R.id.categoryOp);
        dateOp = view.findViewById(R.id.dateOp);
        timeOp = view.findViewById(R.id.timeOp);

        LayoutTransition transition = new LayoutTransition();
        transition.setAnimateParentHierarchy(false);
        bottomSheetContent.setLayoutTransition(transition);
        subList.setLayoutTransition(transition);

        bottomSheetDialog.setContentView(view);
        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setPeekHeight(((Resources.getSystem().getDisplayMetrics().heightPixels) / 3));
        extraSpace.setMinimumHeight((Resources.getSystem().getDisplayMetrics().heightPixels) / 6);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                    dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        edtTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().matches("")) {
                    btnAdd.setEnabled(false);
                    btnAdd.animate().alpha(0.5f);
                } else {
                    btnAdd.setEnabled(true);
                    btnAdd.animate().alpha(1.0f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ((null != edtTaskText.getLayout() && edtTaskText.getLayout().getLineCount() > 5)) {
                    edtTaskText.getText().delete(edtTaskText.getText().length() - 1, edtTaskText.getText().length());
                }
            }
        });

        addASubtaskOption.setOnClickListener(v -> {
            if (subList.getChildCount() < 5) {
                int emptyTextView = notEmpty();
                if (emptyTextView == -1) {
                    View subListAddView = View.inflate(getContext(), R.layout.subtask_add_layout, null);
                    EditText sub = subListAddView.findViewById(R.id.edtAddCategory);
                    CheckBox check = subListAddView.findViewById(R.id.subTaskCheckbox);
                    check.setVisibility(View.GONE);
                    sub.requestFocus();
                    sub.setOnKeyListener((view1, keycode, keyEvent) -> {
                        if (keycode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                            addASubtaskOption.callOnClick();
                        return false;
                    });
                    sub.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.length() == 30)
                                Toast.makeText(getContext(), R.string.max_subtask_len, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    subListAddView.findViewById(R.id.imgDel).setOnClickListener(delView -> {
                        subList.removeView(subListAddView);
                        bottomSheetBehavior.setPeekHeight(bottomSheetBehavior.getPeekHeight() - 200, true);
                        if (addASubtaskOption.getVisibility() != View.VISIBLE)
                            addASubtaskOption.setVisibility(View.VISIBLE);
                    });
                    subList.addView(subListAddView);
                    bottomSheetBehavior.setPeekHeight(bottomSheetBehavior.getPeekHeight() + 200, true);
                    if (subList.getChildCount() == 5) {
                        addASubtaskOption.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getContext(), R.string.have_empty_subtask, Toast.LENGTH_SHORT).show();
                    subList.getChildAt(emptyTextView).findViewById(R.id.edtAddCategory).requestFocus();
                }
            }
        });

        btnAdd.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtTaskText.getWindowToken(), 0);
            addTask();
        });


        ////////////////////
        categoryOp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                popup.getMenuInflater().inflate(R.menu.edit_delete_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.delete) {
                            taskCategory = null;
                            txtCategorySelected.setText(R.string.set);
                        } else {
                            categoryOp.callOnClick();
                        }
                        return false;
                    }
                });
                popup.show();
                return false;
            }
        });
        categoryOp.setOnClickListener(v -> {
            List<Category> categories = CategoryManager.getInstance(getContext()).getAllCategories();
            new CategoryPickerDialog(getContext(), categories,
                    new CategoryPickerDialog.onSelectListener() {
                        @Override
                        public void done(Category category) {
                            taskCategory = category;
                            txtCategorySelected.setText(taskCategory.getName());
                        }
                    })
                    .show(requireActivity().getSupportFragmentManager(), "CategoryPicker");
            categoryOp.setClickable(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    categoryOp.setClickable(true);
                }
            }, 1000);
        });
        ///////////////////////
        dateOp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                popup.getMenuInflater().inflate(R.menu.edit_delete_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.delete) {
                            taskDate = null;
                            txtDateSelected.setText(R.string.set);
                            txtDateSelected.setTextColor(getResources().getColor(R.color.textColor_hint));
                            timeOp.animate().alpha(0.5f);
                        } else {
                            dateOp.callOnClick();
                        }
                        return false;
                    }
                });
                popup.show();
                return false;
            }
        });
        dateOp.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (DatePickerDialog.OnDateSetListener) (view12, year, month, dayOfMonth) -> {
                        taskDate = new DateManager(Calendar.getInstance()).formatDateForDateBase(year, month, dayOfMonth);
                        if (new DateManager(Calendar.getInstance()).isDatePast(taskDate)) {
                            txtDateSelected.setTextColor(getResources().getColor(R.color.error));
                            Toast.makeText(getContext(), R.string.wanado_in_past, Toast.LENGTH_SHORT).show();
                            txtDateSelected.setText(taskDate);
                            if (timeOp.getAlpha() == 1.0f) timeOp.animate().alpha(0.5f);
                            if (taskTime != null)
                                txtTimeSelected.setTextColor(getResources().getColor(R.color.error));
                        } else {
                            txtDateSelected.setTextColor(getResources().getColor(R.color.textColor_hint));
                            txtDateSelected.setText(taskDate);
                            if (timeOp.getAlpha() != 1.0f) timeOp.animate().alpha(1.0f);
                            if (taskTime != null)
                                if (new DateManager(Calendar.getInstance()).isTimePast(taskDate, taskTime)) {
                                    txtTimeSelected.setTextColor(getResources().getColor(R.color.error));
                                } else {
                                    txtTimeSelected.setTextColor(getResources().getColor(R.color.textColor_hint));
                                }
                        }
                    },
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
            dateOp.setClickable(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dateOp.setClickable(true);
                }
            }, 1000);
        });
        /////////
        timeOp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (timeOp.getAlpha() == 1.0f) {
                    PopupMenu popup = new PopupMenu(getContext(), v);
                    popup.getMenuInflater().inflate(R.menu.edit_delete_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.delete) {
                                taskTime = null;
                                txtTimeSelected.setText(R.string.set);
                                txtTimeSelected.setTextColor(getResources().getColor(R.color.textColor_hint));
                            } else {
                                timeOp.callOnClick();
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
                return false;
            }
        });
        timeOp.setOnClickListener(v -> {
            if (timeOp.getAlpha() == 1.0f) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        (TimePickerDialog.OnTimeSetListener) (view13, hourOfDay, minute) -> {
                            taskTime = new DateManager(Calendar.getInstance()).formatTimeForDateBase(hourOfDay, minute);
                            txtTimeSelected.setText(taskTime);
                            if (new DateManager(Calendar.getInstance()).isTimePast(taskDate, taskTime)) {
                                txtTimeSelected.setTextColor(getResources().getColor(R.color.error));
                                Toast.makeText(getContext(), R.string.wanado_in_past, Toast.LENGTH_SHORT).show();
                                txtTimeSelected.setText(taskTime);
                            } else {
                                txtTimeSelected.setTextColor(getResources().getColor(R.color.textColor_hint));
                                txtTimeSelected.setText(taskTime);
                            }
                        },
                        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                        Calendar.getInstance().get(Calendar.MINUTE),
                        true);
                timePickerDialog.show();
                timeOp.setClickable(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        timeOp.setClickable(true);
                    }
                }, 1000);
            } else {
                Toast.makeText(getContext(), R.string.date_first, Toast.LENGTH_SHORT).show();
            }
        });

        return bottomSheetDialog;
    }

    private int notEmpty() {
        for (int i = 0; i < subList.getChildCount(); i++) {
            if (subList.getChildAt(i) != null) {
                EditText editText = subList.getChildAt(i).findViewById(R.id.edtAddCategory);
                if (editText.getText().toString().trim().matches("")) return i;
            }
        }
        return -1;
    }

    private void addTask() {
        String task = edtTaskText.getText().toString().trim();
        List<SubTask> subTasks = subTaskData();
        if (taskDate != null)
            if (new DateManager(Calendar.getInstance()).isDatePast(taskDate)) {
                Toast.makeText(getContext(), getResources().getString(R.string.wrong_date), Toast.LENGTH_SHORT).show();
                txtDateSelected.setTextColor(getResources().getColor(R.color.error));
                txtTimeSelected.setTextColor(getResources().getColor(R.color.error));
            }
        if (taskTime != null)
            if (new DateManager(Calendar.getInstance()).isTimePast(taskDate, taskTime)) {
                Toast.makeText(getContext(), getResources().getString(R.string.wrong_time), Toast.LENGTH_SHORT).show();
                txtTimeSelected.setTextColor(getResources().getColor(R.color.error));
            }
        TaskManager.getInstance(getContext())
                .createTask(new Task(task, (taskCategory == null) ? "-1" : taskCategory.getId(),
                        (taskDate == null) ? "-1" : taskDate,
                        (taskTime == null) ? "-1" : taskTime,
                        subTasks));
        dismiss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (listener != null)
            listener.onDismiss();
        super.onDismiss(dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private List<SubTask> subTaskData() {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        for (int i = 0; i < subList.getChildCount(); i++) {
            if (subList.getChildAt(i) != null) {
                EditText editText = subList.getChildAt(i).findViewById(R.id.edtAddCategory);
                if (!editText.getText().toString().trim().matches("")) {
                    subTasks.add(new SubTask(editText.getText().toString().trim(),false));
                }
            }
        }
        return subTasks;
    }
}
