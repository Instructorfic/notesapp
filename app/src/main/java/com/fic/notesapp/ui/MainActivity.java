package com.fic.notesapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fic.notesapp.R;
import com.fic.notesapp.data.remote.api.ApiClient;
import com.fic.notesapp.data.remote.api.ApiService;
import com.fic.notesapp.data.repository.NoteRepository;
import com.fic.notesapp.domain.NoteInteractor;
import com.fic.notesapp.domain.model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private NoteAdapter noteAdapter;
    private List<Note> noteList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar loadingProgressBar;
    private Toolbar toolbar;

    private NoteInteractor noteInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        noteInteractor = new NoteInteractor(new NoteRepository(ApiClient.getRetrofitInstance().create(ApiService.class)));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FloatingActionButton fabAddNote = findViewById(R.id.fabAddNote);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        toolbar = findViewById(R.id.toolbar);

        fabAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            startActivity(intent);
        });

        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(noteList, this);
        recyclerView.setAdapter(noteAdapter);

        loadNotes();
        swipeRefreshLayout.setOnRefreshListener(this::loadNotes);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new NoteItemTouchHelperCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void loadNotes() {
        loadingProgressBar.setVisibility(ProgressBar.VISIBLE);
        swipeRefreshLayout.setRefreshing(true);

        noteInteractor.loadNotes(new Callback<List<Note>>() {
            @Override
            public void onResponse(Call<List<Note>> call, Response<List<Note>> response) {
                loadingProgressBar.setVisibility(ProgressBar.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Note> notes = response.body();
                    toolbar.setTitle(getString(R.string.notes_size, notes.size()));
                    noteList.clear();
                    noteList.addAll(notes);
                    noteAdapter.notifyDataSetChanged();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "Error en la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Note>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class NoteItemTouchHelperCallback extends ItemTouchHelper.SimpleCallback {

        public NoteItemTouchHelperCallback() {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Note note = noteList.get(position);

            if (direction == ItemTouchHelper.RIGHT) {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra("NOTE_ID", note.getId());
                intent.putExtra("NOTE_TITLE", note.getTitle());
                intent.putExtra("NOTE_CONTENT", note.getContent());
                intent.putExtra("CATEGORY_ID", note.getCategory().getId());
                startActivity(intent);
            } else if (direction == ItemTouchHelper.LEFT) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("¿Está seguro de que desea eliminar esta nota?")
                        .setPositiveButton("Sí", (dialog, which) -> deleteNote(note.getId()))
                        .setNegativeButton("No", (dialog, which) -> noteAdapter.notifyItemChanged(position))
                        .show();
            }
        }
    }

    private void deleteNote(int noteId) {
        noteInteractor.deleteNote(noteId, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    noteList.removeIf(note -> note.getId() == noteId);
                    toolbar.setTitle(getString(R.string.notes_size, noteList.size()));
                    noteAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Nota eliminada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error al eliminar la nota", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadNotes();
    }
}
