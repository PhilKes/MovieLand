<template>
  <v-card class="dialog-card">
    <v-card-title>Add New Movie</v-card-title>
    <v-container>
      <v-row justify="center">
      </v-row>
      <v-row justify="center" >
        <v-data-table
          v-model="selected" show-select
          :loading="loading"
          :headers="headers"
          :items="movies"
          item-key="tmdbId" selectable-key="selectable"
          class="elevation-1"
          :items-per-page="10"
        >
          <template v-slot:top>
            <v-toolbar flat>
              <v-text-field :disabled="loading"
                            v-model="search"
                            label="Search" hide-details
              ></v-text-field>
              <v-btn color="success" @click="searchTmdb" :disabled="search.length<1">
                <v-icon>fas fa-search</v-icon>
              </v-btn>
            </v-toolbar>
          </template>

          <template v-slot:item.name="{ item }">
            <template class="disabled-text">
              {{item.name}}
            </template>
            <v-chip v-if="!item.selectable" disabled>Already Exists</v-chip>
          </template>

          <template v-slot:item.posterUrl="{ item }">
            <v-img :src="item.posterUrl" max-width="50"/>
          </template>

          <template v-slot:item.date="{ item }">
            {{item.date | formatDate}}
          </template>

        </v-data-table>
      </v-row>
    </v-container>
    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn color="secondary" outlined @click="$emit('submit',false)">Cancel</v-btn>
      <v-btn color="success" @click="submit">Add</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
  export default {
    name: "MovieAdd",
    props: {
      tmdbRepo: Object,
      existingMovies: Array
    },
    data() {
      return {
        selected: [],
        search: "",
        movies: [],
        headers: [
          {
            value: 'posterUrl',
            text: 'Poster',
            sortable: false,
            width: '50px'
          },
          {
            value: 'name',
            text: 'Title',
          },
          {
            value: 'date',
            text: 'Date',
          }
        ],
        loading: false
      }
    },
    async mounted() {
      this.loading = true;
      this.movies = await this.tmdbRepo.topMovies();
      this.initMovieRows();
      this.loading = false;
    },
    methods: {
      async searchTmdb() {
        this.loading = true
        this.movies = await this.tmdbRepo.search(this.search);
        this.initMovieRows();
        this.loading = false
      },
      submit() {
        this.$emit('submit', this.selected.map(movie => ({
          /* tmdbId: movie.tmdbId,*/
          name: movie.name
        })))
      },
      initMovieRows() {

        this.movies.forEach((movie, idx) => {
          movie.id = idx;
          movie.selectable=true;
          if (this.existingMovies.some(existingMovie => existingMovie.name === movie.name)) {
            console.log("duplicate", movie.name)
            movie.selectable = false;
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
