<template>
  <v-container fluid>
    <v-skeleton-loader type="card" v-if="loading"/>
    <movie-card v-else :movie="movie" :shows="shows" no-link :show-view="true"/>

  </v-container>
</template>

<script>

  import MovieCard from "../../../components/cards/MovieCard";

  export default {
    name: "MovieDetails",
    components: {
      MovieCard
    },
    data: () => ({
      movie: null,
      shows: [],
      loading: true
    }),
    async fetch() {
      this.movie = await this.$repos.movies.id(this.$route.params.movId);
      this.shows = await this.$repos.shows.showsOfMovieWeek(this.$route.params.movId);
      this.shows.forEach(show => show.date = new Date(show.date));
      console.log(this.movie, this.shows)
      setTimeout(() => this.loading = false, 0);
    }
  }
</script>

<style scoped>

</style>
