import React, { Component } from 'react';
import './App.css';
import Home from './Home';
import {BrowserRouter as Router, Redirect, Route, Switch} from 'react-router-dom';
import MovieList from './components/MovieList';
import MovieShowList from './components/MovieShowList';

class App extends Component {
    render() {
        return (
            <Router>
                <Switch>
                    <Route path='/' exact={true} >
                        <Redirect to='/movies' />
                    </Route>
                    <Route path='/movies' exact={true} component={MovieList}/>
                    <Route path='/shows' exact={true} component={MovieShowList}/>
                </Switch>
            </Router>
        )
    }
}

export default App;