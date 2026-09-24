package com.example.kitpo_l1;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerType;
    private EditText editValue;
    private EditText editIndex;
    private Button btnAdd;
    private Button btnRemove;
    private Button btnGet;
    private Button btnBalance;
    private Button btnSave;
    private Button btnLoad;
    private ListView listView;

    private BinaryTreeView treeView;

    private UserFactory userFactory;
    private ArrayAdapter<String> listAdapter;

    private TreeViewModel viewModel;
    private List<String> typeNames;

    private static final String SAVE_FILE_NAME = "tree_data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   // layout с твоими View

        // ---- ViewModel, переживает поворот экрана ----
        viewModel = new ViewModelProvider(this).get(TreeViewModel.class);

        // ---- Инициализация UI-элементов ----
        spinnerType = findViewById(R.id.spinnerType);
        editValue   = findViewById(R.id.editValue);
        editIndex   = findViewById(R.id.editIndex);
        btnAdd      = findViewById(R.id.btnAdd);
        btnRemove   = findViewById(R.id.btnRemove);
        btnGet      = findViewById(R.id.btnGet);
        btnBalance  = findViewById(R.id.btnBalance);
        btnSave     = findViewById(R.id.btnSave);
        btnLoad     = findViewById(R.id.btnLoad);
        listView    = findViewById(R.id.listView);
        treeView   = findViewById(R.id.treeView);


        // ---- Фабрика типов и список имён ----
        userFactory = new UserFactory();
        typeNames = userFactory.getTypeNameList();

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                typeNames
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // ---- Восстановление состояния после поворота ----
        if (viewModel.currentType == null) {
            // Первый запуск: выбираем первый тип
            if (!typeNames.isEmpty()) {
                selectType(typeNames.get(0), 0);
                spinnerType.setSelection(2);
            }
        } else {
            // Уже есть текущий тип и дерево
            // Ставим Spinner на прошлую позицию
            if (viewModel.selectedIndex >= 0 && viewModel.selectedIndex < typeNames.size()) {
                spinnerType.setSelection(viewModel.selectedIndex);
            }
            // Обновляем список
            refreshList();
        }

        // ---- Реакция на выбор типа в Spinner ----
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = typeNames.get(position);
                selectType(name, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // ---- Адаптер списка значений дерева ----
        listAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                new ArrayList<>());
        listView.setAdapter(listAdapter);

        // ---- Обработчики кнопок ----
        btnAdd.setOnClickListener(v -> onAdd());
        btnRemove.setOnClickListener(v -> onRemove());
        btnGet.setOnClickListener(v -> onGet());
        btnBalance.setOnClickListener(v -> onBalance());
        btnSave.setOnClickListener(v -> onSave());
        btnLoad.setOnClickListener(v -> onLoad());
    }

    // ----- выбор типа данных -----
    private void selectType(String typeName, int index) {
        UserType t = userFactory.getBuilderByName(typeName);
        if (t == null) {
            Toast.makeText(this, "Тип не найден: " + typeName, Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.selectedIndex = index;

        if (viewModel.currentType == null) {
            // Первый раз инициализируем тип и дерево
            viewModel.currentType = t;
            viewModel.initIfNeeded(t);
        } else {
            // Если поворот экрана — тип уже есть, дерево уже создано
            // Менять тип на другой здесь можно, но это нарушит согласованность данных
            // Поэтому, если имя такое же — ничего не делаем,
            // если пользователь сам сменит тип, будем считать, что он начал "новую" работу.
            if (!viewModel.currentType.getClass().equals(t.getClass())) {
                // Начинаем новую структуру для другого типа данных
                viewModel.currentType = t;
                viewModel.tree = new BinaryTree(t.getTypeComparator());
            }
        }

        refreshList();
    }

    // ----- добавление элемента -----
    private void onAdd() {
        if (viewModel.currentType == null || viewModel.tree == null) return;

        String text = editValue.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "Введите значение", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Object value = viewModel.currentType.parseValue(text);
            viewModel.tree.add(value);
            refreshList();
            editValue.setText("");
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // ----- удаление по индексу -----
    private void onRemove() {
        if (viewModel.tree == null) return;

        String idxStr = editIndex.getText().toString().trim();
        if (idxStr.isEmpty()) {
            Toast.makeText(this, "Введите индекс", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            int index = Integer.parseInt(idxStr);
            viewModel.tree.remove(index);
            refreshList();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Индекс должен быть числом", Toast.LENGTH_SHORT).show();
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this, "Неверный индекс", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // ----- получение по индексу -----
    private void onGet() {
        if (viewModel.tree == null) return;

        String idxStr = editIndex.getText().toString().trim();
        if (idxStr.isEmpty()) {
            Toast.makeText(this, "Введите индекс", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            int index = Integer.parseInt(idxStr);
            Object value = viewModel.tree.get(index);
            Toast.makeText(this, "Элемент: " + value, Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Индекс должен быть числом", Toast.LENGTH_SHORT).show();
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this, "Неверный индекс", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // ----- балансировка дерева -----
    private void onBalance() {
        if (viewModel.tree == null) return;
        try {
            viewModel.tree.balance();
            refreshList();
            Toast.makeText(this, "Дерево сбалансировано", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка balance(): " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    // ----- сохранение в файл -----
    // ----- сохранение в файл -----
    private void onSave() {
        if (viewModel.tree == null) {
            Toast.makeText(this, "Дерево пустое, нечего сохранять", Toast.LENGTH_SHORT).show();
            return;
        }
        try (OutputStreamWriter writer =
                     new OutputStreamWriter(openFileOutput(SAVE_FILE_NAME, MODE_PRIVATE))) {

            viewModel.tree.saveTo(writer);
            Toast.makeText(this, "Сохранено в " + SAVE_FILE_NAME, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, "Ошибка сохранения: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // ----- загрузка из файла -----
    private void onLoad() {
        if (viewModel.currentType == null) {
            Toast.makeText(this, "Сначала выберите тип данных", Toast.LENGTH_SHORT).show();
            return;
        }
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(openFileInput(SAVE_FILE_NAME)))) {

            // ВАЖНО: использовать результат статического loadFrom!
            viewModel.tree = BinaryTree.loadFrom(reader, viewModel.currentType);

            refreshList();
            Toast.makeText(this, "Загружено из " + SAVE_FILE_NAME, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, "Ошибка загрузки: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    // ----- обновление ListView -----
    private void refreshList() {
        if (viewModel.tree == null) return;
        List<Object> list = viewModel.tree.toList();
        ArrayList<String> asStrings = new ArrayList<>();
        for (Object o : list) {
            asStrings.add(String.valueOf(o));
        }
        listAdapterClearAndSet(asStrings);
        if (treeView != null) {
            treeView.setTree(viewModel.tree);
        }

    }

    private void listAdapterClearAndSet(List<String> items) {
        if (listAdapter == null) {
            listAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1,
                    new ArrayList<>());
            listView.setAdapter(listAdapter);
        }
        listAdapter.clear();
        listAdapter.addAll(items);
        listAdapter.notifyDataSetChanged();
    }
}
