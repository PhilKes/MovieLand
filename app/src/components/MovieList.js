import React, {Component} from 'react';
import {Button, ButtonGroup, Container, Table, Input, Alert} from 'reactstrap';
import AppNavbar from '../AppNavbar';
import { Link } from 'react-router-dom';
import MovieModal from "./modal/MovieModal";
import Moment from 'moment';
import axios from "axios";

const INSTRUCTOR = 'admin';
const PASSWORD = 'admin';

/** /movies page Component
 * Shows Movies + ADD/REMOVE Movies*/
class MovieList extends Component {

    constructor(props) {
        super(props);
        this.state = {movies: [], isLoading: true,error: ""};
        this.remove = this.remove.bind(this);
        this.searchQuery="";
    }

    /** Initial load all shows*/
    componentDidMount() {
        this.setState({isLoading: true});

        axios.get('api/movies')
            .then(res => res.data)
            .then(data => this.setState({movies: data, isLoading: false}));
    }

    /** Remove movie with movieid=id*/
    async remove(id) {
        await axios.delete(`/api/movie/${id}`)
            .then(() => {
            let updatedMovies = [...this.state.movies].filter(i => i.movId !== id);
            this.setState({movies: updatedMovies});
        });
    }

    /** Add movie from Modal entered name and reload shows*/
    addMovie(movie) {
        console.log("Add " +movie.name);
        var result=
            axios.post(`/api/movie`, JSON.stringify(movie),
                {
                    headers: {
                'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    }
                })
                .then(response => {
                    console.log(response)
                //TODO Catch exception if movie already there
                    return axios.get('api/movies?name=' + this.searchQuery.value)
                        .then(resp => {
                            this.setState({movies: resp.data, isLoading: false})
                    });
                })
                .catch(err => {
                    console.log(err.response.data.msg);
                    this.setState({error: err.response.data.msg});

                });
    }

    /** Submit search if pressed enter in searchquery field*/
    handleKeyPress(ev){
        if(ev.charCode==13){ //Enter pressed?
            console.log("Search: "+this.searchQuery.value);
            axios.get('api/movies?name=' + this.searchQuery.value)
                .then(resp => this.setState({movies: resp.data}));
        }
    }

    render() {
        const {movies, isLoading, error} = this.state;
        if (isLoading) {
            return <p>Loading...</p>;
        }
        const movieList = movies.map(movie => {
            const descript = `${movie.description}`;
            return <tr key={movie.movId}>
                <td><img src={movie.posterUrl} className={'img-fluid'} alt="Responsive image"/></td>
                <td >{movie.name}</td>
                <td>{descript}</td>
                <td>
                   <div key={movie.movId}>{Moment(movie.date).format('DD.MM.YYYY')}</div>
                </td>
                <td>
                    <ButtonGroup>
                       {/* <Button size="sm" color="primary" tag={Link} to={"/shows/" + movie.movId}>Edit</Button>*/}
                        <Button size="sm" color="danger" onClick={() => this.remove(movie.movId)}>Delete</Button>
                    </ButtonGroup>
                </td>
            </tr>
        });


        return (

            <div>
                <AppNavbar/>
                <Container fluid>
                    <div className="float-right">
                        <MovieModal onSubmit={this.addMovie.bind(this)}/>
                    </div>
                    <h2>Movies</h2><br/>
                    <Input type="text" placeholder="Search Movies" innerRef={ref=>this.searchQuery=ref} onKeyPress={this.handleKeyPress.bind(this)} /><br/>
                    <Alert color="danger" isOpen={error.length>0} toggle={()=>this.setState({error: ""})}>
                        {error}
                    </Alert>
                    <Table className="mt-5">
                        <thead>
                        <tr>
                            <th width="15%">Poster</th>
                            <th width="6%">Name</th>
                            <th width="25%">Description</th>
                            <th width="15%">Release Date</th>
                            <th width="15%">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {movieList}
                        </tbody>
                    </Table>
                </Container>
            </div>
        );
    }
}

export default MovieList;