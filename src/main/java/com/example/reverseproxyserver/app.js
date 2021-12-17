const express = require('express')
const app = express()
const port = 3000

app.get('/', (req, res) => {
    res.send({
        task: "send JSON in proper way",
        status: 1999,
    })
})

app.get('/relative-path', (req, res) => {
    res.send('Relative path is working, brudda!')
})

app.post('/', (req, res) => {
    console.log(123)
    console.log(req.body)
    res.send(req.body)
})

app.listen(port, () => {
    console.log(`Example app listening at http://localhost:${port}`)
})