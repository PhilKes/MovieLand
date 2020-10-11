<template>
  <v-container>
    <v-row justify="center">
      <h4>Movies Dashboard</h4>
    </v-row>
    <v-row justify="center">
      <v-col cols="12" md="10">
        <v-data-table :loading="loading" :headers="headers" :items="movies" v-model="selected"
                      item-key="movId" class="elevation-1" sort-desc sort-by="date"
                      :search="search" :items-per-page="10" show-select
        >
          <template v-slot:top>
            <v-toolbar flat>
              <v-text-field
                v-model="search"
                label="Search TMDB Movies" hide-details append-icon="fas fa-search"
              ></v-text-field>
              <v-spacer/>
              <v-btn color="error" @click="deleteSelected" class="mr-2" :disabled="selected.length < 1">
                <v-icon>fas fa-trash</v-icon>
              </v-btn>
              <v-btn color="success" @click="addMovie">
                <v-icon>fas fa-plus</v-icon>
              </v-btn>
            </v-toolbar>
          </template>

          <template v-slot:item.posterUrl="{ item }">
            <v-img :src="item.posterUrl" max-width="80"/>
          </template>

          <template v-slot:item.date="{ item }">
            {{item.date | formatDate}}
          </template>

          <template v-slot:item.actions="{ item }">
           <!-- <v-btn icon @click="editMovie(item)" class="mr-2">
              <v-icon>
                mdi-pencil
              </v-icon>
            </v-btn>-->
            <v-btn color="error" elevation="0" small fab @click="deleteMovie(item)">
              <v-icon>
                mdi-delete
              </v-icon>
            </v-btn>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
  import MovieDelete from "../../components/forms/movie/MovieDelete";
  import MovieAdd from "../../components/forms/movie/MovieAdd";
  import Utils from "../../service/Utils";
  import MovieEdit from "../../components/forms/movie/MovieEdit";

  export default {
    name: "movies",
    data() {
      return {
        movies: [],
        headers: [
          {
            value: 'posterUrl',
            text: 'Poster',
            sortable: false,
            width: '80px'
          },
          {
            value: 'name',
            text: 'Title',
          },
          {
            value: 'date',
            text: 'Date',
          },
          {text: 'Actions', value: 'actions', sortable: false},
          /* {
             value: 'description',
             text: 'Description',
             sortable: false
           }*/
        ],
        selected: [],
        loading: true,
        search: ""
      }
    },
    async fetch() {
      this.movies = await this.$repos.movies.all();
      this.movies.forEach((movie, idx) => {
        movie.idx = idx;
        movie.exists = true;
      });
      console.log("movies", this.movies)
      this.loading = false
    },
    methods: {
      async editMovie(movie) {
        let editedMovie = await this.$dialog.showAndWait(MovieEdit, {movie})
        if (editedMovie !== false) {
          this.loading = true;
          this.$repos.movies.update(editedMovie).then(updatedMovie => {
            updatedMovie.idx=movie.idx;
            this.movies[updatedMovie.idx]=updatedMovie;
            //Utils.initIndex(this.movies)
            this.$dialog.message.success('Movies succesfully updated!',
              {position: 'bottom-left', timeout: 3000})
            console.debug("Updated",editedMovie)
            this.loading = false;
          })
        }
      },
      async deleteMovie(movie) {
        let del = await this.$dialog.showAndWait(MovieDelete, {title: movie.name})
        if (del === true) {
          this.loading = true;
          this.$repos.movies.remove(movie.id).then(resp => {
            this.movies.splice(movie.idx, 1);
            Utils.initIndex(this.movies)
            this.$dialog.message.success('Movies succesfully removed!',
              {position: 'bottom-left', timeout: 3000})
            console.debug("Deleted",movie)
            this.loading = false;
          })
        }
      },
      async deleteSelected() {
        const titles = this.selected.length === 1 ? this.selected[0].name : this.selected.map(movie => movie.name);
        let del = await this.$dialog.showAndWait(MovieDelete, {title: titles})
        if (del === true) {
          this.loading = true;
          let promises = [];
          this.selected.forEach(delMovie => {
            promises.push(this.$repos.movies.remove(delMovie.id).then(resp => {
              return delMovie.idx;
            }));
          })
          Promise.all(promises).then(removedIdxs => {
            removedIdxs.forEach(removeIdx =>
              this.movies.splice(this.movies.indexOf(movie => movie.idx === removeIdx), 1)
            );
            Utils.initIndex(this.movies);
            this.$dialog.message.success('Movies succesfully removed!',
              {position: 'bottom-left', timeout: 3000})
            console.debug("Deleted",this.selected)
            this.loading = false;
          })
        }
      },
      async addMovie() {
        let newMovies = await this.$dialog.showAndWait(MovieAdd,
          {width: '620', tmdbRepo: this.$repos.tmdb, existingMovies: this.movies})
        if (newMovies !== false) {
          this.loading = true;
          let promises = [];
          newMovies.forEach(newMovie => {
            promises.push(this.$repos.movies.add(newMovie).then(addedMovie => {
              this.movies.push(addedMovie)
              return addedMovie
            }));
          });
          Promise.all(promises).then(addedMovies => {
            this.$dialog.message.success('Movies succesfully added!',
              {position: 'bottom-left', timeout: 3000})
            console.debug("Added",addedMovies)
            this.loading = false;
          })
          //Scroll to new movie
        }
      }
    }
  }
</script>

<style scoped>
</style>
