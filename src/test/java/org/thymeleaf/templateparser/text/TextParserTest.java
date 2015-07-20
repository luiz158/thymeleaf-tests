/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */
package org.thymeleaf.templateparser.text;

import java.io.CharArrayReader;
import java.util.List;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;


/*
 *
 * @author Daniel Fernandez
 * @since 2.0.0
 */
public class TextParserTest extends TestCase {

    private static int totalTestExecutions = 0;
    

    public void test() throws Exception {


        testDoc(
            "",
            "[]");
        testDoc(
            "<div class= s>",
            "[T(<div class= s>){1,1}]");
        testDoc(
            "<html>",
            "[T(<html>){1,1}]");
        testDoc(
            "<html></html>",
            "[T(<html></html>){1,1}]");
        testDoc(
            "<html><body></html>",
            "[T(<html><body></html>){1,1}]");
        testDoc(
            "<html>",
            "[T(<html>){1,1}]");
        testDoc(
            "<html></html>",
            "[T(<html></html>){1,1}]");
        testDoc(
            "<html><body></html>",
            "[T(<html><body></html>){1,1}]");
        testDoc(
            "<html><title><body><p>",
            "[T(<html><title><body><p>){1,1}]");
        testDoc(
            "[[title]][[body]][[p]]",
            "[T([[title]][[body]][[p]]){1,1}]");
        testDoc(
            "[[#hello]]...[[/hello]]",
            "[OES(hello){1,1}OEE(hello){1,9}T(...){1,11}CES(hello){1,14}CEE(hello){1,22}]");
        testDoc(
            "...[[#hello]]...[[/hello]]...",
            "[T(...){1,1}OES(hello){1,4}OEE(hello){1,12}T(...){1,14}CES(hello){1,17}CEE(hello){1,25}T(...){1,27}]");
        testDoc(
            "...[[#hello]]...[[#bye/]][[/hello]]...",
            "[T(...){1,1}OES(hello){1,4}OEE(hello){1,12}T(...){1,14}SES(bye){1,17}SEE(bye){1,23}CES(hello){1,26}CEE(hello){1,34}T(...){1,36}]");
        testDoc(
            "...[[#hello src=\"hello\"]]...[[#bye/]][[/hello]]...",
            "[T(...){1,1}OES(hello){1,4}A(src){1,13}(=){1,16}(\"hello\"){1,17}OEE(hello){1,24}T(...){1,26}SES(bye){1,29}SEE(bye){1,35}CES(hello){1,38}CEE(hello){1,46}T(...){1,48}]");
        testDoc(
            "...[[#hello src=\"hello\"]]...[[#bye alt=\"hello\"/]][[/hello]]...",
            "[T(...){1,1}OES(hello){1,4}A(src){1,13}(=){1,16}(\"hello\"){1,17}OEE(hello){1,24}T(...){1,26}SES(bye){1,29}A(alt){1,36}(=){1,39}(\"hello\"){1,40}SEE(bye){1,47}CES(hello){1,50}CEE(hello){1,58}T(...){1,60}]");
        testDoc(
            "...[[#hello   src=\"hello\"  ]]...[[#bye alt=\"hello\"  /]][[/hello]]...",
            "[T(...){1,1}OES(hello){1,4}A(src){1,15}(=){1,18}(\"hello\"){1,19}OEE(hello){1,28}T(...){1,30}SES(bye){1,33}A(alt){1,40}(=){1,43}(\"hello\"){1,44}SEE(bye){1,53}CES(hello){1,56}CEE(hello){1,64}T(...){1,66}]");
        testDoc(
            "...[[#hello \nsrc=\"hello\" ]]...[[#bye   /]][[/hello]]...",
            "[T(...){1,1}OES(hello){1,4}A(src){2,1}(=){2,4}(\"hello\"){2,5}OEE(hello){2,13}T(...){2,15}SES(bye){2,18}SEE(bye){2,27}CES(hello){2,30}CEE(hello){2,38}T(...){2,40}]");
        testDocError(
                "...[[#hello \nsrc=\"hello\" bee ]]...[[#bye alt  /]][[/hello]]...",
                null,
                2, 16);
        testDoc(
                "[[#hello]][[/hello]]",
                "[OES(hello){1,1}OEE(hello){1,9}CES(hello){1,11}CEE(hello){1,19}]");
        testDoc(
            "[[#hello/]]",
            "[SES(hello){1,1}SEE(hello){1,9}]");
        testDoc(
            "[[#]][[/]]",
            "[OES(){1,1}OEE(){1,4}CES(){1,6}CEE(){1,9}]");
        testDoc(
            "[[#/]]",
            "[SES(){1,1}SEE(){1,4}]");
        testDoc(
            "...[[#   src=\"hello\"  ]]...[[# alt=\"hello\"  /]][[/]]...",
            "[T(...){1,1}OES(){1,4}A(src){1,10}(=){1,13}(\"hello\"){1,14}OEE(){1,23}T(...){1,25}SES(){1,28}A(alt){1,32}(=){1,35}(\"hello\"){1,36}SEE(){1,45}CES(){1,48}CEE(){1,51}T(...){1,53}]");
        testDocError(
            "...[[#   src=\"hello\"  ]]...[[# alt=\"hello\"  /]]...",
            null, -1, -1);
/*
        testDoc(
            "<p><img src=\"hello\">Something</p>",
            "[OES(p){1,1}OEE(p){1,3}NSES(img){1,4}IWS( ){1,8}A(src){1,9}(=){1,12}(\"hello\"){1,13}NSEE(img){1,20}T(Something){1,21}CES(p){1,30}CEE(p){1,33}]",
            noRestrictionsAutoClose);
        testDoc(
            "<table><tr>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}AOES(body){1,1}AOEE(body){1,1}OES(table){1,1}OEE(table){1,7}AOES(tbody){1,8}AOEE(tbody){1,8}OES(tr){1,8}OEE(tr){1,11}ACES(tr){1,12}ACEE(tr){1,12}ACES(tbody){1,12}ACEE(tbody){1,12}ACES(table){1,12}ACEE(table){1,12}ACES(body){1,12}ACEE(body){1,12}ACES(html){1,12}ACEE(html){1,12}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<tr>",
            "[AOES(tbody){1,1}AOEE(tbody){1,1}OES(tr){1,1}OEE(tr){1,4}ACES(tr){1,5}ACEE(tr){1,5}ACES(tbody){1,5}ACEE(tbody){1,5}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<table><tfoot><tr>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}AOES(body){1,1}AOEE(body){1,1}OES(table){1,1}OEE(table){1,7}OES(tfoot){1,8}OEE(tfoot){1,14}OES(tr){1,15}OEE(tr){1,18}ACES(tr){1,19}ACEE(tr){1,19}ACES(tfoot){1,19}ACEE(tfoot){1,19}ACES(table){1,19}ACEE(table){1,19}ACES(body){1,19}ACEE(body){1,19}ACES(html){1,19}ACEE(html){1,19}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<table><thead><tr>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}AOES(body){1,1}AOEE(body){1,1}OES(table){1,1}OEE(table){1,7}OES(thead){1,8}OEE(thead){1,14}OES(tr){1,15}OEE(tr){1,18}ACES(tr){1,19}ACEE(tr){1,19}ACES(thead){1,19}ACEE(thead){1,19}ACES(table){1,19}ACEE(table){1,19}ACES(body){1,19}ACEE(body){1,19}ACES(html){1,19}ACEE(html){1,19}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<table><tr><td><tr>",
            "[OES(table){1,1}OEE(table){1,7}OES(tr){1,8}OEE(tr){1,11}OES(td){1,12}OEE(td){1,15}ACES(td){1,16}ACEE(td){1,16}ACES(tr){1,16}ACEE(tr){1,16}OES(tr){1,16}OEE(tr){1,19}ACES(tr){1,20}ACEE(tr){1,20}ACES(table){1,20}ACEE(table){1,20}]",
            noRestrictionsAutoClose);
        testDoc(
            "<table><col>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}AOES(body){1,1}AOEE(body){1,1}OES(table){1,1}OEE(table){1,7}AOES(colgroup){1,8}AOEE(colgroup){1,8}NSES(col){1,8}NSEE(col){1,12}ACES(colgroup){1,13}ACEE(colgroup){1,13}ACES(table){1,13}ACEE(table){1,13}ACES(body){1,13}ACEE(body){1,13}ACES(html){1,13}ACEE(html){1,13}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<table><thead><td><col>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}AOES(body){1,1}AOEE(body){1,1}OES(table){1,1}OEE(table){1,7}OES(thead){1,8}OEE(thead){1,14}OES(td){1,15}OEE(td){1,18}ACES(td){1,19}ACEE(td){1,19}ACES(thead){1,19}ACEE(thead){1,19}AOES(colgroup){1,19}AOEE(colgroup){1,19}NSES(col){1,19}NSEE(col){1,23}ACES(colgroup){1,24}ACEE(colgroup){1,24}ACES(table){1,24}ACEE(table){1,24}ACES(body){1,24}ACEE(body){1,24}ACES(html){1,24}ACEE(html){1,24}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<meta>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}NSES(meta){1,1}NSEE(meta){1,6}ACES(head){1,7}ACEE(head){1,7}ACES(html){1,7}ACEE(html){1,7}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<html><meta>",
            "[OES(html){1,1}OEE(html){1,6}AOES(head){1,7}AOEE(head){1,7}NSES(meta){1,7}NSEE(meta){1,12}ACES(head){1,13}ACEE(head){1,13}ACES(html){1,13}ACEE(html){1,13}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<head><meta>",
            "[AOES(html){1,1}AOEE(html){1,1}OES(head){1,1}OEE(head){1,6}NSES(meta){1,7}NSEE(meta){1,12}ACES(head){1,13}ACEE(head){1,13}ACES(html){1,13}ACEE(html){1,13}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<body><meta>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}OES(body){1,1}OEE(body){1,6}NSES(meta){1,7}NSEE(meta){1,12}ACES(body){1,13}ACEE(body){1,13}ACES(html){1,13}ACEE(html){1,13}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<title>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}OES(title){1,1}OEE(title){1,7}ACES(title){1,8}ACEE(title){1,8}ACES(head){1,8}ACEE(head){1,8}AOES(body){1,8}AOEE(body){1,8}ACES(body){1,8}ACEE(body){1,8}ACES(html){1,8}ACEE(html){1,8}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<head><title>",
            "[AOES(html){1,1}AOEE(html){1,1}OES(head){1,1}OEE(head){1,6}OES(title){1,7}OEE(title){1,13}ACES(title){1,14}ACEE(title){1,14}ACES(head){1,14}ACEE(head){1,14}AOES(body){1,14}AOEE(body){1,14}ACES(body){1,14}ACEE(body){1,14}ACES(html){1,14}ACEE(html){1,14}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<html><title>",
            "[OES(html){1,1}OEE(html){1,6}AOES(head){1,7}AOEE(head){1,7}OES(title){1,7}OEE(title){1,13}ACES(title){1,14}ACEE(title){1,14}ACES(head){1,14}ACEE(head){1,14}AOES(body){1,14}AOEE(body){1,14}ACES(body){1,14}ACEE(body){1,14}ACES(html){1,14}ACEE(html){1,14}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<body><title>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}OES(body){1,1}OEE(body){1,6}OES(title){1,7}OEE(title){1,13}ACES(title){1,14}ACEE(title){1,14}ACES(body){1,14}ACEE(body){1,14}ACES(html){1,14}ACEE(html){1,14}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<div><div><title>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}AOES(body){1,1}AOEE(body){1,1}OES(div){1,1}OEE(div){1,5}OES(div){1,6}OEE(div){1,10}OES(title){1,11}OEE(title){1,17}ACES(title){1,18}ACEE(title){1,18}ACES(div){1,18}ACEE(div){1,18}ACES(div){1,18}ACEE(div){1,18}ACES(body){1,18}ACEE(body){1,18}ACES(html){1,18}ACEE(html){1,18}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<html><object>",
            "[OES(html){1,1}OEE(html){1,6}AOES(head){1,7}AOEE(head){1,7}OES(object){1,7}OEE(object){1,14}ACES(object){1,15}ACEE(object){1,15}ACES(head){1,15}ACEE(head){1,15}AOES(body){1,15}AOEE(body){1,15}ACES(body){1,15}ACEE(body){1,15}ACES(html){1,15}ACEE(html){1,15}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<body><object>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}OES(body){1,1}OEE(body){1,6}OES(object){1,7}OEE(object){1,14}ACES(object){1,15}ACEE(object){1,15}ACES(body){1,15}ACEE(body){1,15}ACES(html){1,15}ACEE(html){1,15}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<template> if (a < 0) { do this} </template>",
            "[OES(template){1,1}OEE(template){1,10}T( if (a < 0) { do this} ){1,11}CES(template){1,34}CEE(template){1,44}]",
            noRestrictionsAutoClose);
        testDoc(
            "<script> if (a < 0) { do this} </script>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}OES(script){1,1}OEE(script){1,8}T( if (a < 0) { do this} ){1,9}CES(script){1,32}CEE(script){1,40}ACES(head){1,41}ACEE(head){1,41}AOES(body){1,41}AOEE(body){1,41}ACES(body){1,41}ACEE(body){1,41}ACES(html){1,41}ACEE(html){1,41}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<body><script> if (a < 0) { do this} </script>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}OES(body){1,1}OEE(body){1,6}OES(script){1,7}OEE(script){1,14}T( if (a < 0) { do this} ){1,15}CES(script){1,38}CEE(script){1,46}ACES(body){1,47}ACEE(body){1,47}ACES(html){1,47}ACEE(html){1,47}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<template> if (a < 0) { do this} </template>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}OES(template){1,1}OEE(template){1,10}T( if (a < 0) { do this} ){1,11}CES(template){1,34}CEE(template){1,44}ACES(head){1,45}ACEE(head){1,45}AOES(body){1,45}AOEE(body){1,45}ACES(body){1,45}ACEE(body){1,45}ACES(html){1,45}ACEE(html){1,45}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<body><template> if (a < 0) { do this} </template>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}OES(body){1,1}OEE(body){1,6}OES(template){1,7}OEE(template){1,16}T( if (a < 0) { do this} ){1,17}CES(template){1,40}CEE(template){1,50}ACES(body){1,51}ACEE(body){1,51}ACES(html){1,51}ACEE(html){1,51}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<body><template><p>something</p></template>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}OES(body){1,1}OEE(body){1,6}OES(template){1,7}OEE(template){1,16}OES(p){1,17}OEE(p){1,19}T(something){1,20}CES(p){1,29}CEE(p){1,32}CES(template){1,33}CEE(template){1,43}ACES(body){1,44}ACEE(body){1,44}ACES(html){1,44}ACEE(html){1,44}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<head>",
            "[AOES(html){1,1}AOEE(html){1,1}OES(head){1,1}OEE(head){1,6}ACES(head){1,7}ACEE(head){1,7}AOES(body){1,7}AOEE(body){1,7}ACES(body){1,7}ACEE(body){1,7}ACES(html){1,7}ACEE(html){1,7}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<div><head>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}AOES(body){1,1}AOEE(body){1,1}OES(div){1,1}OEE(div){1,5}OES(head){1,6}OEE(head){1,11}ACES(head){1,12}ACEE(head){1,12}ACES(div){1,12}ACEE(div){1,12}ACES(body){1,12}ACEE(body){1,12}ACES(html){1,12}ACEE(html){1,12}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<body>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}OES(body){1,1}OEE(body){1,6}ACES(body){1,7}ACEE(body){1,7}ACES(html){1,7}ACEE(html){1,7}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<div><body>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}AOES(body){1,1}AOEE(body){1,1}OES(div){1,1}OEE(div){1,5}OES(body){1,6}OEE(body){1,11}ACES(body){1,12}ACEE(body){1,12}ACES(div){1,12}ACEE(div){1,12}ACES(body){1,12}ACEE(body){1,12}ACES(html){1,12}ACEE(html){1,12}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<h1>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}AOES(body){1,1}AOEE(body){1,1}OES(h1){1,1}OEE(h1){1,4}ACES(h1){1,5}ACEE(h1){1,5}ACES(body){1,5}ACEE(body){1,5}ACES(html){1,5}ACEE(html){1,5}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<html><h1>",
            "[OES(html){1,1}OEE(html){1,6}AOES(head){1,7}AOEE(head){1,7}ACES(head){1,7}ACEE(head){1,7}AOES(body){1,7}AOEE(body){1,7}OES(h1){1,7}OEE(h1){1,10}ACES(h1){1,11}ACEE(h1){1,11}ACES(body){1,11}ACEE(body){1,11}ACES(html){1,11}ACEE(html){1,11}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<footer>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}AOES(body){1,1}AOEE(body){1,1}OES(footer){1,1}OEE(footer){1,8}ACES(footer){1,9}ACEE(footer){1,9}ACES(body){1,9}ACEE(body){1,9}ACES(html){1,9}ACEE(html){1,9}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<html><footer>",
            "[OES(html){1,1}OEE(html){1,6}AOES(head){1,7}AOEE(head){1,7}ACES(head){1,7}ACEE(head){1,7}AOES(body){1,7}AOEE(body){1,7}OES(footer){1,7}OEE(footer){1,14}ACES(footer){1,15}ACEE(footer){1,15}ACES(body){1,15}ACEE(body){1,15}ACES(html){1,15}ACEE(html){1,15}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<p>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}AOES(body){1,1}AOEE(body){1,1}OES(p){1,1}OEE(p){1,3}ACES(p){1,4}ACEE(p){1,4}ACES(body){1,4}ACEE(body){1,4}ACES(html){1,4}ACEE(html){1,4}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<html><p>",
            "[OES(html){1,1}OEE(html){1,6}AOES(head){1,7}AOEE(head){1,7}ACES(head){1,7}ACEE(head){1,7}AOES(body){1,7}AOEE(body){1,7}OES(p){1,7}OEE(p){1,9}ACES(p){1,10}ACEE(p){1,10}ACES(body){1,10}ACEE(body){1,10}ACES(html){1,10}ACEE(html){1,10}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<div><p>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}AOES(body){1,1}AOEE(body){1,1}OES(div){1,1}OEE(div){1,5}OES(p){1,6}OEE(p){1,8}ACES(p){1,9}ACEE(p){1,9}ACES(div){1,9}ACEE(div){1,9}ACES(body){1,9}ACEE(body){1,9}ACES(html){1,9}ACEE(html){1,9}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<html><p>",
            "[OES(html){1,1}OEE(html){1,6}AOES(head){1,7}AOEE(head){1,7}ACES(head){1,7}ACEE(head){1,7}AOES(body){1,7}AOEE(body){1,7}OES(p){1,7}OEE(p){1,9}ACES(p){1,10}ACEE(p){1,10}ACES(body){1,10}ACEE(body){1,10}ACES(html){1,10}ACEE(html){1,10}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<head><p>",
            "[AOES(html){1,1}AOEE(html){1,1}OES(head){1,1}OEE(head){1,6}ACES(head){1,7}ACEE(head){1,7}AOES(body){1,7}AOEE(body){1,7}OES(p){1,7}OEE(p){1,9}ACES(p){1,10}ACEE(p){1,10}ACES(body){1,10}ACEE(body){1,10}ACES(html){1,10}ACEE(html){1,10}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<ul>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}AOES(body){1,1}AOEE(body){1,1}OES(ul){1,1}OEE(ul){1,4}ACES(ul){1,5}ACEE(ul){1,5}ACES(body){1,5}ACEE(body){1,5}ACES(html){1,5}ACEE(html){1,5}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<html><ul>",
            "[OES(html){1,1}OEE(html){1,6}AOES(head){1,7}AOEE(head){1,7}ACES(head){1,7}ACEE(head){1,7}AOES(body){1,7}AOEE(body){1,7}OES(ul){1,7}OEE(ul){1,10}ACES(ul){1,11}ACEE(ul){1,11}ACES(body){1,11}ACEE(body){1,11}ACES(html){1,11}ACEE(html){1,11}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<li>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}AOES(body){1,1}AOEE(body){1,1}OES(li){1,1}OEE(li){1,4}ACES(li){1,5}ACEE(li){1,5}ACES(body){1,5}ACEE(body){1,5}ACES(html){1,5}ACEE(html){1,5}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<html><li>",
            "[OES(html){1,1}OEE(html){1,6}AOES(head){1,7}AOEE(head){1,7}ACES(head){1,7}ACEE(head){1,7}AOES(body){1,7}AOEE(body){1,7}OES(li){1,7}OEE(li){1,10}ACES(li){1,11}ACEE(li){1,11}ACES(body){1,11}ACEE(body){1,11}ACES(html){1,11}ACEE(html){1,11}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<figure>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}AOES(body){1,1}AOEE(body){1,1}OES(figure){1,1}OEE(figure){1,8}ACES(figure){1,9}ACEE(figure){1,9}ACES(body){1,9}ACEE(body){1,9}ACES(html){1,9}ACEE(html){1,9}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<html><figure>",
            "[OES(html){1,1}OEE(html){1,6}AOES(head){1,7}AOEE(head){1,7}ACES(head){1,7}ACEE(head){1,7}AOES(body){1,7}AOEE(body){1,7}OES(figure){1,7}OEE(figure){1,14}ACES(figure){1,15}ACEE(figure){1,15}ACES(body){1,15}ACEE(body){1,15}ACES(html){1,15}ACEE(html){1,15}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<table><tr><th><tr>",
            "[OES(table){1,1}OEE(table){1,7}OES(tr){1,8}OEE(tr){1,11}OES(th){1,12}OEE(th){1,15}ACES(th){1,16}ACEE(th){1,16}ACES(tr){1,16}ACEE(tr){1,16}OES(tr){1,16}OEE(tr){1,19}ACES(tr){1,20}ACEE(tr){1,20}ACES(table){1,20}ACEE(table){1,20}]",
            noRestrictionsAutoClose);
        testDoc(
            "<code>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}AOES(body){1,1}AOEE(body){1,1}OES(code){1,1}OEE(code){1,6}ACES(code){1,7}ACEE(code){1,7}ACES(body){1,7}ACEE(body){1,7}ACES(html){1,7}ACEE(html){1,7}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<html><code>",
            "[OES(html){1,1}OEE(html){1,6}AOES(head){1,7}AOEE(head){1,7}ACES(head){1,7}ACEE(head){1,7}AOES(body){1,7}AOEE(body){1,7}OES(code){1,7}OEE(code){1,12}ACES(code){1,13}ACEE(code){1,13}ACES(body){1,13}ACEE(body){1,13}ACES(html){1,13}ACEE(html){1,13}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<html><body><code>",
            "[OES(html){1,1}OEE(html){1,6}AOES(head){1,7}AOEE(head){1,7}ACES(head){1,7}ACEE(head){1,7}OES(body){1,7}OEE(body){1,12}OES(code){1,13}OEE(code){1,18}ACES(code){1,19}ACEE(code){1,19}ACES(body){1,19}ACEE(body){1,19}ACES(html){1,19}ACEE(html){1,19}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<br>",
            "[AOES(html){1,1}AOEE(html){1,1}AOES(head){1,1}AOEE(head){1,1}ACES(head){1,1}ACEE(head){1,1}AOES(body){1,1}AOEE(body){1,1}NSES(br){1,1}NSEE(br){1,4}ACES(body){1,5}ACEE(body){1,5}ACES(html){1,5}ACEE(html){1,5}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<html><br>",
            "[OES(html){1,1}OEE(html){1,6}AOES(head){1,7}AOEE(head){1,7}ACES(head){1,7}ACEE(head){1,7}AOES(body){1,7}AOEE(body){1,7}NSES(br){1,7}NSEE(br){1,10}ACES(body){1,11}ACEE(body){1,11}ACES(html){1,11}ACEE(html){1,11}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<html><body><br>",
            "[OES(html){1,1}OEE(html){1,6}AOES(head){1,7}AOEE(head){1,7}ACES(head){1,7}ACEE(head){1,7}OES(body){1,7}OEE(body){1,12}NSES(br){1,13}NSEE(br){1,16}ACES(body){1,17}ACEE(body){1,17}ACES(html){1,17}ACEE(html){1,17}]",
            noRestrictionsAutoOpenClose);
        testDoc(
            "<td><tr>",
            "[OES(td){1,1}OEE(td){1,4}ACES(td){1,5}ACEE(td){1,5}OES(tr){1,5}OEE(tr){1,8}ACES(tr){1,9}ACEE(tr){1,9}]",
            noRestrictionsAutoClose);
        testDoc(
            "<th><tr>",
            "[OES(th){1,1}OEE(th){1,4}ACES(th){1,5}ACEE(th){1,5}OES(tr){1,5}OEE(tr){1,8}ACES(tr){1,9}ACEE(tr){1,9}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, World!",
            "[T(ello, Worl){1,1}]",
            "[T(ello, Worl){1,1}]",
            1, 10,
            noRestrictionsAutoClose);
        testDoc(
            "This is simply a text",
            "[T(This is simply a text){1,1}]",
            "[T(This is simply a text){1,1}]",
            noRestrictionsAutoClose);
        testDoc(
            "<img src=\"hello\">Something",
            "[NSES(img){1,1}IWS( ){1,5}A(src){1,6}(=){1,9}(\"hello\"){1,10}NSEE(img){1,17}T(Something){1,18}]",
            noRestrictionsAutoClose);
        testDoc(
            "<p><img src=\"hello\">Something</p>",
            "[OES(p){1,1}OEE(p){1,3}NSES(img){1,4}IWS( ){1,8}A(src){1,9}(=){1,12}(\"hello\"){1,13}NSEE(img){1,20}T(Something){1,21}CES(p){1,30}CEE(p){1,33}]",
            noRestrictionsAutoClose);
        testDoc(
            "<ul><li>a",
            "[OES(ul){1,1}OEE(ul){1,4}OES(li){1,5}OEE(li){1,8}T(a){1,9}ACES(li){1,10}ACEE(li){1,10}ACES(ul){1,10}ACEE(ul){1,10}]",
            noRestrictionsAutoClose);
        testDoc(
            "<ul><li>a</ul>",
            "[OES(ul){1,1}OEE(ul){1,4}OES(li){1,5}OEE(li){1,8}T(a){1,9}ACES(li){1,10}ACEE(li){1,10}CES(ul){1,10}CEE(ul){1,14}]",
            noRestrictionsAutoClose);
        testDoc(
            "<ul><li>a</ul>",
            "[OES(ul){1,1}OEE(ul){1,4}OES(li){1,5}OEE(li){1,8}T(a){1,9}CES(ul){1,10}CEE(ul){1,14}]",
            noRestrictions);
        testDoc(
            "<ul><li>a<li>b</ul>",
            "[OES(ul){1,1}OEE(ul){1,4}OES(li){1,5}OEE(li){1,8}T(a){1,9}ACES(li){1,10}ACEE(li){1,10}OES(li){1,10}OEE(li){1,13}T(b){1,14}ACES(li){1,15}ACEE(li){1,15}CES(ul){1,15}CEE(ul){1,19}]",
            noRestrictionsAutoClose);
        testDoc(
            "<ul><li>a<span>lala<li>b</ul>",
            "[OES(ul){1,1}OEE(ul){1,4}OES(li){1,5}OEE(li){1,8}T(a){1,9}OES(span){1,10}OEE(span){1,15}T(lala){1,16}ACES(span){1,20}ACEE(span){1,20}ACES(li){1,20}ACEE(li){1,20}OES(li){1,20}OEE(li){1,23}T(b){1,24}ACES(li){1,25}ACEE(li){1,25}CES(ul){1,25}CEE(ul){1,29}]",
            noRestrictionsAutoClose);
        testDoc(
            "<table><thead><tr><th>A",
            "[OES(table){1,1}OEE(table){1,7}OES(thead){1,8}OEE(thead){1,14}OES(tr){1,15}OEE(tr){1,18}OES(th){1,19}OEE(th){1,22}T(A){1,23}ACES(th){1,24}ACEE(th){1,24}ACES(tr){1,24}ACEE(tr){1,24}ACES(thead){1,24}ACEE(thead){1,24}ACES(table){1,24}ACEE(table){1,24}]",
            noRestrictionsAutoClose);
        testDoc(
            "<table><thead><tr><th>A<th>B",
            "[OES(table){1,1}OEE(table){1,7}OES(thead){1,8}OEE(thead){1,14}OES(tr){1,15}OEE(tr){1,18}OES(th){1,19}OEE(th){1,22}T(A){1,23}ACES(th){1,24}ACEE(th){1,24}OES(th){1,24}OEE(th){1,27}T(B){1,28}ACES(th){1,29}ACEE(th){1,29}ACES(tr){1,29}ACEE(tr){1,29}ACES(thead){1,29}ACEE(thead){1,29}ACES(table){1,29}ACEE(table){1,29}]",
            noRestrictionsAutoClose);
        testDoc(
            "<table><thead><tr><th>A<td>B",
            "[OES(table){1,1}OEE(table){1,7}OES(thead){1,8}OEE(thead){1,14}OES(tr){1,15}OEE(tr){1,18}OES(th){1,19}OEE(th){1,22}T(A){1,23}ACES(th){1,24}ACEE(th){1,24}OES(td){1,24}OEE(td){1,27}T(B){1,28}ACES(td){1,29}ACEE(td){1,29}ACES(tr){1,29}ACEE(tr){1,29}ACES(thead){1,29}ACEE(thead){1,29}ACES(table){1,29}ACEE(table){1,29}]",
            noRestrictionsAutoClose);
        testDoc(
            "<table><thead><tr><td>A<th>B",
            "[OES(table){1,1}OEE(table){1,7}OES(thead){1,8}OEE(thead){1,14}OES(tr){1,15}OEE(tr){1,18}OES(td){1,19}OEE(td){1,22}T(A){1,23}ACES(td){1,24}ACEE(td){1,24}OES(th){1,24}OEE(th){1,27}T(B){1,28}ACES(th){1,29}ACEE(th){1,29}ACES(tr){1,29}ACEE(tr){1,29}ACES(thead){1,29}ACEE(thead){1,29}ACES(table){1,29}ACEE(table){1,29}]",
            noRestrictionsAutoClose);
        testDoc(
            "<table><thead><tr><td><p>P<th>B",
            "[OES(table){1,1}OEE(table){1,7}OES(thead){1,8}OEE(thead){1,14}OES(tr){1,15}OEE(tr){1,18}OES(td){1,19}OEE(td){1,22}OES(p){1,23}OEE(p){1,25}T(P){1,26}ACES(p){1,27}ACEE(p){1,27}ACES(td){1,27}ACEE(td){1,27}OES(th){1,27}OEE(th){1,30}T(B){1,31}ACES(th){1,32}ACEE(th){1,32}ACES(tr){1,32}ACEE(tr){1,32}ACES(thead){1,32}ACEE(thead){1,32}ACES(table){1,32}ACEE(table){1,32}]",
            noRestrictionsAutoClose);
        testDoc(
            "<table><thead><tr><tbody><td><p>P<th>B",
            "[OES(table){1,1}OEE(table){1,7}OES(thead){1,8}OEE(thead){1,14}OES(tr){1,15}OEE(tr){1,18}ACES(tr){1,19}ACEE(tr){1,19}ACES(thead){1,19}ACEE(thead){1,19}OES(tbody){1,19}OEE(tbody){1,25}OES(td){1,26}OEE(td){1,29}OES(p){1,30}OEE(p){1,32}T(P){1,33}ACES(p){1,34}ACEE(p){1,34}ACES(td){1,34}ACEE(td){1,34}OES(th){1,34}OEE(th){1,37}T(B){1,38}ACES(th){1,39}ACEE(th){1,39}ACES(tbody){1,39}ACEE(tbody){1,39}ACES(table){1,39}ACEE(table){1,39}]",
            noRestrictionsAutoClose);
        testDoc(
            "<li>A<li>B",
            "[OES(li){1,1}OEE(li){1,4}T(A){1,5}ACES(li){1,6}ACEE(li){1,6}OES(li){1,6}OEE(li){1,9}T(B){1,10}ACES(li){1,11}ACEE(li){1,11}]",
            noRestrictionsAutoClose);
        testDoc(
            "<li>A<li>B",
            "[OES(li){1,1}OEE(li){1,4}T(A){1,5}OES(li){1,6}OEE(li){1,9}T(B){1,10}]",
            noRestrictions);
        testDoc(
            "<table><th:block><tr><td>A<tr><td>B<td>C",
            "[OES(table){1,1}OEE(table){1,7}OES(th:block){1,8}OEE(th:block){1,17}OES(tr){1,18}OEE(tr){1,21}OES(td){1,22}OEE(td){1,25}T(A){1,26}ACES(td){1,27}ACEE(td){1,27}ACES(tr){1,27}ACEE(tr){1,27}OES(tr){1,27}OEE(tr){1,30}OES(td){1,31}OEE(td){1,34}T(B){1,35}ACES(td){1,36}ACEE(td){1,36}OES(td){1,36}OEE(td){1,39}T(C){1,40}ACES(td){1,41}ACEE(td){1,41}ACES(tr){1,41}ACEE(tr){1,41}ACES(th:block){1,41}ACEE(th:block){1,41}ACES(table){1,41}ACEE(table){1,41}]",
            noRestrictionsAutoClose);
        testDoc(
            "<table><th:block><tr><td>A<tr><td>B<td>C<tfoot>",
            "[OES(table){1,1}OEE(table){1,7}OES(th:block){1,8}OEE(th:block){1,17}OES(tr){1,18}OEE(tr){1,21}OES(td){1,22}OEE(td){1,25}T(A){1,26}ACES(td){1,27}ACEE(td){1,27}ACES(tr){1,27}ACEE(tr){1,27}OES(tr){1,27}OEE(tr){1,30}OES(td){1,31}OEE(td){1,34}T(B){1,35}ACES(td){1,36}ACEE(td){1,36}OES(td){1,36}OEE(td){1,39}T(C){1,40}ACES(td){1,41}ACEE(td){1,41}ACES(tr){1,41}ACEE(tr){1,41}OES(tfoot){1,41}OEE(tfoot){1,47}ACES(tfoot){1,48}ACEE(tfoot){1,48}ACES(th:block){1,48}ACEE(th:block){1,48}ACES(table){1,48}ACEE(table){1,48}]",
            noRestrictionsAutoClose);
        testDoc(
            "<ul><li><ol><li>A<li>B",
            "[OES(ul){1,1}OEE(ul){1,4}OES(li){1,5}OEE(li){1,8}OES(ol){1,9}OEE(ol){1,12}OES(li){1,13}OEE(li){1,16}T(A){1,17}ACES(li){1,18}ACEE(li){1,18}OES(li){1,18}OEE(li){1,21}T(B){1,22}ACES(li){1,23}ACEE(li){1,23}ACES(ol){1,23}ACEE(ol){1,23}ACES(li){1,23}ACEE(li){1,23}ACES(ul){1,23}ACEE(ul){1,23}]",
            noRestrictionsAutoClose);
        testDoc(
            "<p>A<p>B",
            "[OES(p){1,1}OEE(p){1,3}T(A){1,4}ACES(p){1,5}ACEE(p){1,5}OES(p){1,5}OEE(p){1,7}T(B){1,8}ACES(p){1,9}ACEE(p){1,9}]",
            noRestrictionsAutoClose);
        testDoc(
            "<p>A<h1>B",
            "[OES(p){1,1}OEE(p){1,3}T(A){1,4}ACES(p){1,5}ACEE(p){1,5}OES(h1){1,5}OEE(h1){1,8}T(B){1,9}ACES(h1){1,10}ACEE(h1){1,10}]",
            noRestrictionsAutoClose);
        testDoc(
            "<p>A<div>B",
            "[OES(p){1,1}OEE(p){1,3}T(A){1,4}ACES(p){1,5}ACEE(p){1,5}OES(div){1,5}OEE(div){1,9}T(B){1,10}ACES(div){1,11}ACEE(div){1,11}]",
            noRestrictionsAutoClose);
        testDoc(
            "<p>A<hr>B",
            "[OES(p){1,1}OEE(p){1,3}T(A){1,4}ACES(p){1,5}ACEE(p){1,5}NSES(hr){1,5}NSEE(hr){1,8}T(B){1,9}]",
            noRestrictionsAutoClose);
        testDoc(
            "<p>A<hr/>B",
            "[OES(p){1,1}OEE(p){1,3}T(A){1,4}ACES(p){1,5}ACEE(p){1,5}SES(hr){1,5}SEE(hr){1,8}T(B){1,10}]",
            noRestrictionsAutoClose);
        testDoc(
            "<br a>Hello",
            "[OES(br){1,1}IWS( ){1,4}A(a){1,5}(){1,6}(){1,6}OEE(br){1,6}T(Hello){1,7}]",
            "[OE(br[a='']){1,1}T(Hello){1,7}]",
            noRestrictions);
        testDoc(
            "<br a b>Hello",
            "[OES(br){1,1}IWS( ){1,4}A(a){1,5}(){1,6}(){1,6}IWS( ){1,6}A(b){1,7}(){1,8}(){1,8}OEE(br){1,8}T(Hello){1,9}]",
            "[OE(br[a='',b='']){1,1}T(Hello){1,9}]",
            noRestrictions);
        testDocError(
            "<li a=\"11\"\">Hello</li>",
            null,
            null,
            1, 1,
            noRestrictions);
        testDoc(
            "<li a=\"a < 0\">Hello</li>",
            "[OES(li){1,1}IWS( ){1,4}A(a){1,5}(=){1,6}(\"a < 0\"){1,7}OEE(li){1,14}T(Hello){1,15}CES(li){1,20}CEE(li){1,24}]",
            "[OE(li[a='a < 0']){1,1}T(Hello){1,15}CE(li){1,20}]",
            noRestrictions);
        testDoc(
            "<script> var a = \"<div>\" if (a < 0)</script>",
            "[OES(script){1,1}OEE(script){1,8}T( var a = \"){1,9}OES(div){1,19}OEE(div){1,23}T(\" if (a < 0)){1,24}CES(script){1,36}CEE(script){1,44}]",
            "[OE(script){1,1}T( var a = \"){1,9}OE(div){1,19}T(\" if (a < 0)){1,24}CE(script){1,36}]",
            noRestrictions);
        testDoc(
            "<style> a = \"<div>\" if (a < 0)</style>",
            "[OES(style){1,1}OEE(style){1,7}T( a = \"){1,8}OES(div){1,14}OEE(div){1,18}T(\" if (a < 0)){1,19}CES(style){1,31}CEE(style){1,38}]",
            "[OE(style){1,1}T( a = \"){1,8}OE(div){1,14}T(\" if (a < 0)){1,19}CE(style){1,31}]",
            noRestrictions);
        testDoc(
                "<script> var a = \"<div>\" if (a < 0)</script>",
                "[OES(script){1,1}OEE(script){1,8}T( var a = \"<div>\" if (a < 0)){1,9}CES(script){1,36}CEE(script){1,44}]",
                ParseConfiguration.htmlConfiguration());
        testDoc(
            "<style> a = \"<div>\" if (a < 0)</style>",
            "[OES(style){1,1}OEE(style){1,7}T( a = \"<div>\" if (a < 0)){1,8}CES(style){1,31}CEE(style){1,38}]",
            ParseConfiguration.htmlConfiguration());
        testDoc(
            "<script> var a = \"<div>\"\n\nif (a < 0)</script>",
            "[OES(script){1,1}OEE(script){1,8}T( var a = \"<div>\"\n\nif (a < 0)){1,9}CES(script){3,11}CEE(script){3,19}]",
            ParseConfiguration.htmlConfiguration());
        testDoc(
            "<style> a = \"<div>\"\n\nif (a < 0)</style>",
            "[OES(style){1,1}OEE(style){1,7}T( a = \"<div>\"\n\nif (a < 0)){1,8}CES(style){3,11}CEE(style){3,18}]",
            ParseConfiguration.htmlConfiguration());
        testDoc(
            "<SCRIPT> var a = \"<div>\" if (a < 0)</SCRIPT>",
            "[OES(SCRIPT){1,1}OEE(SCRIPT){1,8}T( var a = \"){1,9}OES(div){1,19}OEE(div){1,23}T(\" if (a < 0)){1,24}CES(SCRIPT){1,36}CEE(SCRIPT){1,44}]",
            "[OE(SCRIPT){1,1}T( var a = \"){1,9}OE(div){1,19}T(\" if (a < 0)){1,24}CE(SCRIPT){1,36}]",
            noRestrictions);
        testDoc(
            "<STYLE> a = \"<div>\" if (a < 0)</STYLE>",
            "[OES(STYLE){1,1}OEE(STYLE){1,7}T( a = \"){1,8}OES(div){1,14}OEE(div){1,18}T(\" if (a < 0)){1,19}CES(STYLE){1,31}CEE(STYLE){1,38}]",
            "[OE(STYLE){1,1}T( a = \"){1,8}OE(div){1,14}T(\" if (a < 0)){1,19}CE(STYLE){1,31}]",
            noRestrictions);
        testDoc(
            "<SCRIPT> var a = \"<div>\" if (a < 0)</SCRIPT>",
            "[OES(SCRIPT){1,1}OEE(SCRIPT){1,8}T( var a = \"<div>\" if (a < 0)){1,9}CES(SCRIPT){1,36}CEE(SCRIPT){1,44}]",
            ParseConfiguration.htmlConfiguration());
        testDoc(
            "<STYLE> a = \"<div>\" if (a < 0)</STYLE>",
            "[OES(STYLE){1,1}OEE(STYLE){1,7}T( a = \"<div>\" if (a < 0)){1,8}CES(STYLE){1,31}CEE(STYLE){1,38}]",
            ParseConfiguration.htmlConfiguration());
        testDoc(
            "<SCRIPT> var a = \"<div>\"\n\nif (a < 0)</SCRIPT>",
            "[OES(SCRIPT){1,1}OEE(SCRIPT){1,8}T( var a = \"<div>\"\n\nif (a < 0)){1,9}CES(SCRIPT){3,11}CEE(SCRIPT){3,19}]",
            ParseConfiguration.htmlConfiguration());
        testDoc(
            "<STYLE> a = \"<div>\"\n\nif (a < 0)</STYLE>",
            "[OES(STYLE){1,1}OEE(STYLE){1,7}T( a = \"<div>\"\n\nif (a < 0)){1,8}CES(STYLE){3,11}CEE(STYLE){3,18}]",
            ParseConfiguration.htmlConfiguration());
        testDoc(
            "<script type=\"text/javascript\"> var a = \"<div>\" if (a < 0)</script>",
            "[OES(script){1,1}IWS( ){1,8}A(type){1,9}(=){1,13}(\"text/javascript\"){1,14}OEE(script){1,31}T( var a = \"){1,32}OES(div){1,42}OEE(div){1,46}T(\" if (a < 0)){1,47}CES(script){1,59}CEE(script){1,67}]",
            "[OE(script[type='text/javascript']){1,1}T( var a = \"){1,32}OE(div){1,42}T(\" if (a < 0)){1,47}CE(script){1,59}]",
            noRestrictions);
        testDoc(
            "<style type=\"text/css\"> a = \"<div>\" if (a < 0)</style>",
            "[OES(style){1,1}IWS( ){1,7}A(type){1,8}(=){1,12}(\"text/css\"){1,13}OEE(style){1,23}T( a = \"){1,24}OES(div){1,30}OEE(div){1,34}T(\" if (a < 0)){1,35}CES(style){1,47}CEE(style){1,54}]",
            "[OE(style[type='text/css']){1,1}T( a = \"){1,24}OE(div){1,30}T(\" if (a < 0)){1,35}CE(style){1,47}]",
            noRestrictions);
        testDoc(
            "<script type=\"text/javascript\"> var a = \"<div>\" if (a < 0)</script>",
            "[OES(script){1,1}IWS( ){1,8}A(type){1,9}(=){1,13}(\"text/javascript\"){1,14}OEE(script){1,31}T( var a = \"<div>\" if (a < 0)){1,32}CES(script){1,59}CEE(script){1,67}]",
            ParseConfiguration.htmlConfiguration());
        testDoc(
            "<style type=\"text/css\"> a = \"<div>\" if (a < 0)</style>",
            "[OES(style){1,1}IWS( ){1,7}A(type){1,8}(=){1,12}(\"text/css\"){1,13}OEE(style){1,23}T( a = \"<div>\" if (a < 0)){1,24}CES(style){1,47}CEE(style){1,54}]",
            ParseConfiguration.htmlConfiguration());
        testDoc(
            "<script type=\"text/javascript\"> var a = \"<div>\"\n\nif (a < 0)</script>",
            "[OES(script){1,1}IWS( ){1,8}A(type){1,9}(=){1,13}(\"text/javascript\"){1,14}OEE(script){1,31}T( var a = \"<div>\"\n\nif (a < 0)){1,32}CES(script){3,11}CEE(script){3,19}]",
            ParseConfiguration.htmlConfiguration());
        testDoc(
            "<style type=\"text/css\"> a = \"<div>\"\n\nif (a < 0)</style>",
            "[OES(style){1,1}IWS( ){1,7}A(type){1,8}(=){1,12}(\"text/css\"){1,13}OEE(style){1,23}T( a = \"<div>\"\n\nif (a < 0)){1,24}CES(style){3,11}CEE(style){3,18}]",
            ParseConfiguration.htmlConfiguration());
        testDoc(
            "<SCRIPT type=\"text/javascript\"> var a = \"<div>\" if (a < 0)</SCRIPT>",
            "[OES(SCRIPT){1,1}IWS( ){1,8}A(type){1,9}(=){1,13}(\"text/javascript\"){1,14}OEE(SCRIPT){1,31}T( var a = \"){1,32}OES(div){1,42}OEE(div){1,46}T(\" if (a < 0)){1,47}CES(SCRIPT){1,59}CEE(SCRIPT){1,67}]",
            "[OE(SCRIPT[type='text/javascript']){1,1}T( var a = \"){1,32}OE(div){1,42}T(\" if (a < 0)){1,47}CE(SCRIPT){1,59}]",
            noRestrictions);
        testDoc(
            "<STYLE type=\"text/css\"> a = \"<div>\" if (a < 0)</STYLE>",
            "[OES(STYLE){1,1}IWS( ){1,7}A(type){1,8}(=){1,12}(\"text/css\"){1,13}OEE(STYLE){1,23}T( a = \"){1,24}OES(div){1,30}OEE(div){1,34}T(\" if (a < 0)){1,35}CES(STYLE){1,47}CEE(STYLE){1,54}]",
            "[OE(STYLE[type='text/css']){1,1}T( a = \"){1,24}OE(div){1,30}T(\" if (a < 0)){1,35}CE(STYLE){1,47}]",
            noRestrictions);
        testDoc(
            "<SCRIPT type=\"text/javascript\"> var a = \"<div>\" if (a < 0)</SCRIPT>",
            "[OES(SCRIPT){1,1}IWS( ){1,8}A(type){1,9}(=){1,13}(\"text/javascript\"){1,14}OEE(SCRIPT){1,31}T( var a = \"<div>\" if (a < 0)){1,32}CES(SCRIPT){1,59}CEE(SCRIPT){1,67}]",
            ParseConfiguration.htmlConfiguration());
        testDoc(
            "<STYLE type=\"text/css\"> a = \"<div>\" if (a < 0)</STYLE>",
            "[OES(STYLE){1,1}IWS( ){1,7}A(type){1,8}(=){1,12}(\"text/css\"){1,13}OEE(STYLE){1,23}T( a = \"<div>\" if (a < 0)){1,24}CES(STYLE){1,47}CEE(STYLE){1,54}]",
            ParseConfiguration.htmlConfiguration());
        testDoc(
            "<SCRIPT type=\"text/javascript\"> var a = \"<div>\"\n\nif (a < 0)</SCRIPT>",
            "[OES(SCRIPT){1,1}IWS( ){1,8}A(type){1,9}(=){1,13}(\"text/javascript\"){1,14}OEE(SCRIPT){1,31}T( var a = \"<div>\"\n\nif (a < 0)){1,32}CES(SCRIPT){3,11}CEE(SCRIPT){3,19}]",
            ParseConfiguration.htmlConfiguration());
        testDoc(
            "<STYLE type=\"text/css\"> a = \"<div>\"\n\nif (a < 0)</STYLE>",
            "[OES(STYLE){1,1}IWS( ){1,7}A(type){1,8}(=){1,12}(\"text/css\"){1,13}OEE(STYLE){1,23}T( a = \"<div>\"\n\nif (a < 0)){1,24}CES(STYLE){3,11}CEE(STYLE){3,18}]",
            ParseConfiguration.htmlConfiguration());
        testDoc(
            "<ScripT type=\"text/javascript\"> var a = \"<div>\" if (a < 0)</ScripT>",
            "[OES(ScripT){1,1}IWS( ){1,8}A(type){1,9}(=){1,13}(\"text/javascript\"){1,14}OEE(ScripT){1,31}T( var a = \"<div>\" if (a < 0)){1,32}CES(ScripT){1,59}CEE(ScripT){1,67}]",
            ParseConfiguration.htmlConfiguration());
        testDoc(
            "<StylE type=\"text/css\"> a = \"<div>\" if (a < 0)</StylE>",
            "[OES(StylE){1,1}IWS( ){1,7}A(type){1,8}(=){1,12}(\"text/css\"){1,13}OEE(StylE){1,23}T( a = \"<div>\" if (a < 0)){1,24}CES(StylE){1,47}CEE(StylE){1,54}]",
            ParseConfiguration.htmlConfiguration());
        testDoc(
            "<h1 a=\"if (a < 0)\">Hello</ h1>",
            "[OES(h1){1,1}IWS( ){1,4}A(a){1,5}(=){1,6}(\"if (a < 0)\"){1,7}OEE(h1){1,19}T(Hello</ h1>){1,20}ACES(h1){1,31}ACEE(h1){1,31}]",
            "[OE(h1[a='if (a < 0)']){1,1}T(Hello</ h1>){1,20}ACE(h1){1,31}]",
            noRestrictionsAutoClose);
        testDoc(
            "<h1>Hello</ h1>",
            "[OES(h1){1,1}OEE(h1){1,4}T(Hello</ h1>){1,5}ACES(h1){1,16}ACEE(h1){1,16}]",
            "[OE(h1){1,1}T(Hello</ h1>){1,5}ACE(h1){1,16}]",
            noRestrictionsAutoClose);
        testDoc(
            "<h1>Hello</ h1>",
            "[OES(h1){1,1}OEE(h1){1,4}T(Hello</ h1>){1,5}]",
            "[OE(h1){1,1}T(Hello</ h1>){1,5}]",
            noRestrictions);
        testDoc(
            "<p><h1>Hello</ h1></p>",
            "[OES(p){1,1}OEE(p){1,3}OES(h1){1,4}OEE(h1){1,7}T(Hello</ h1>){1,8}ACES(h1){1,19}ACEE(h1){1,19}CES(p){1,19}CEE(p){1,22}]",
            "[OE(p){1,1}OE(h1){1,4}T(Hello</ h1>){1,8}ACE(h1){1,19}CE(p){1,19}]",
            noRestrictionsAutoClose);
        testDoc(
            "<p><h1>Hello</ h1></p>",
            "[OES(p){1,1}OEE(p){1,3}OES(h1){1,4}OEE(h1){1,7}T(Hello</ h1>){1,8}CES(p){1,19}CEE(p){1,22}]",
            "[OE(p){1,1}OE(h1){1,4}T(Hello</ h1>){1,8}CE(p){1,19}]",
            noRestrictions);
        testDoc(
            "Hello, World!",
            "[T(Hello, World!){1,1}]",
            "[T(Hello, World!){1,1}]",
            noRestrictionsAutoClose);
        testDoc(
            "<p>Hello</p>",
            "[OES(p){1,1}OEE(p){1,3}T(Hello){1,4}CES(p){1,9}CEE(p){1,12}]",
            "[OE(p){1,1}T(Hello){1,4}CE(p){1,9}]",
            noRestrictionsAutoClose);
        testDoc(
            "<h1>Hello</h1>",
            "[OES(h1){1,1}OEE(h1){1,4}T(Hello){1,5}CES(h1){1,10}CEE(h1){1,14}]",
            "[OE(h1){1,1}T(Hello){1,5}CE(h1){1,10}]",
            noRestrictionsAutoClose);
        testDoc(
            "<h1>Hello</h1 >",
            "[OES(h1){1,1}OEE(h1){1,4}T(Hello){1,5}CES(h1){1,10}IWS( ){1,14}CEE(h1){1,15}]",
            "[OE(h1){1,1}T(Hello){1,5}CE(h1){1,10}]",
            noRestrictionsAutoClose);
        testDoc(
            "<h1>Hello</h1 \n\n>",
            "[OES(h1){1,1}OEE(h1){1,4}T(Hello){1,5}CES(h1){1,10}IWS( \n\n){1,14}CEE(h1){3,1}]",
            "[OE(h1){1,1}T(Hello){1,5}CE(h1){1,10}]",
            noRestrictionsAutoClose);
        testDoc(
            "<\np  >Hello</p>",
            "[T(<\np  >Hello){1,1}UCES(p){2,10}UCEE(p){2,13}]",
            "[T(<\np  >Hello){1,1}UCE(p){2,10}]",
            noRestrictionsAutoClose);
        testDoc(
            "< h1  >Hello</h1>",
            "[T(< h1  >Hello){1,1}UCES(h1){1,13}UCEE(h1){1,17}]",
            "[T(< h1  >Hello){1,1}UCE(h1){1,13}]",
            noRestrictionsAutoClose);
        testDoc(
            "<h1>Hello</ h1>",
            "[OES(h1){1,1}OEE(h1){1,4}T(Hello</ h1>){1,5}ACES(h1){1,16}ACEE(h1){1,16}]",
            "[OE(h1){1,1}T(Hello</ h1>){1,5}ACE(h1){1,16}]",
            noRestrictionsAutoClose);
        testDoc(
            "< h1>Hello</ h1>",
            "[T(< h1>Hello</ h1>){1,1}]",
            "[T(< h1>Hello</ h1>){1,1}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, World!",
            "[T(ello, Worl){1,1}]",
            "[T(ello, Worl){1,1}]",
            1, 10,
            noRestrictionsAutoClose);
        testDoc(
            "Hello, World!",
            "[T(e){1,1}]",
            "[T(e){1,1}]",
            1, 1,
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <p>lala</p>",
            "[T(Hello, ){1,1}OES(p){1,8}OEE(p){1,10}T(lala){1,11}CES(p){1,15}CEE(p){1,18}]",
            "[T(Hello, ){1,1}OE(p){1,8}T(lala){1,11}CE(p){1,15}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <p>lal'a</p>",
            "[T(Hello, ){1,1}OES(p){1,8}OEE(p){1,10}T(lal'a){1,11}CES(p){1,16}CEE(p){1,19}]",
            "[T(Hello, ){1,1}OE(p){1,8}T(lal'a){1,11}CE(p){1,16}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <p>l'al'a</p>",
            "[T(Hello, ){1,1}OES(p){1,8}OEE(p){1,10}T(l'al'a){1,11}CES(p){1,17}CEE(p){1,20}]",
            "[T(Hello, ){1,1}OE(p){1,8}T(l'al'a){1,11}CE(p){1,17}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <p>lala</p>",
            "[T(o, ){1,1}OES(p){1,4}OEE(p){1,6}T(l){1,7}ACES(p){1,8}ACEE(p){1,8}]",
            "[T(o, ){1,1}OE(p){1,4}T(l){1,7}ACE(p){1,8}]",
            4, 7,
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br/>",
            "[T(Hello, ){1,1}SES(br){1,8}SEE(br){1,11}]",
            "[T(Hello, ){1,1}SE(br){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br th:text=\"ll\"/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}(=){1,19}(\"ll\"){1,20}SEE(br){1,24}]",
            "[T(Hello, ){1,1}SE(br[th:text='ll']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br th:text='ll'/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}(=){1,19}('ll'){1,20}SEE(br){1,24}]",
            "[T(Hello, ){1,1}SE(br[th:text='ll']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br th:text =\"ll\"/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}( =){1,19}(\"ll\"){1,21}SEE(br){1,25}]",
            "[T(Hello, ){1,1}SE(br[th:text='ll']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br th:text =   \"ll\"/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}( =   ){1,19}(\"ll\"){1,24}SEE(br){1,28}]",
            "[T(Hello, ){1,1}SE(br[th:text='ll']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br th:text =   ll/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}( =   ){1,19}(ll){1,24}SEE(br){1,26}]",
            "[T(Hello, ){1,1}SE(br[th:text='ll']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br th:text =   \"ll\"a=2/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}( =   ){1,19}(\"ll\"){1,24}A(a){1,28}(=){1,29}(2){1,30}SEE(br){1,31}]",
            "[T(Hello, ){1,1}SE(br[th:text='ll',a='2']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br th:text =   'll'a=2/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}( =   ){1,19}('ll'){1,24}A(a){1,28}(=){1,29}(2){1,30}SEE(br){1,31}]",
            "[T(Hello, ){1,1}SE(br[th:text='ll',a='2']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br th:text =   \"ll\"a= \n 2/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}( =   ){1,19}(\"ll\"){1,24}A(a){1,28}(= \n ){1,29}(2){2,2}SEE(br){2,3}]",
            "[T(Hello, ){1,1}SE(br[th:text='ll',a='2']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br th:text =   ll a= \n 2/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}( =   ){1,19}(ll){1,24}IWS( ){1,26}A(a){1,27}(= \n ){1,28}(2){2,2}SEE(br){2,3}]",
            "[T(Hello, ){1,1}SE(br[th:text='ll',a='2']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br th:text =   ll a/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}( =   ){1,19}(ll){1,24}IWS( ){1,26}A(a){1,27}(){1,28}(){1,28}SEE(br){1,28}]",
            "[T(Hello, ){1,1}SE(br[th:text='ll',a='']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br th:text =   ll a=/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}( =   ){1,19}(ll){1,24}IWS( ){1,26}A(a){1,27}(=){1,28}(){1,29}SEE(br){1,29}]",
            "[T(Hello, ){1,1}SE(br[th:text='ll',a='']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br th:text = a=/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}( = ){1,19}(a=){1,22}SEE(br){1,24}]",
            "[T(Hello, ){1,1}SE(br[th:text='a=']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br th:text = a= b/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}( = ){1,19}(a=){1,22}IWS( ){1,24}A(b){1,25}(){1,26}(){1,26}SEE(br){1,26}]",
            "[T(Hello, ){1,1}SE(br[th:text='a=',b='']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br th:text = a=b/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}( = ){1,19}(a=b){1,22}SEE(br){1,25}]",
            "[T(Hello, ){1,1}SE(br[th:text='a=b']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br th:text = \"a=b\"/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}( = ){1,19}(\"a=b\"){1,22}SEE(br){1,27}]",
            "[T(Hello, ){1,1}SE(br[th:text='a=b']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br th:text = \"a= b\"/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}( = ){1,19}(\"a= b\"){1,22}SEE(br){1,28}]",
            "[T(Hello, ){1,1}SE(br[th:text='a= b']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br th:text = 'a= b'/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}( = ){1,19}('a= b'){1,22}SEE(br){1,28}]",
            "[T(Hello, ){1,1}SE(br[th:text='a= b']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br th:text = \"a= b\"\n/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}( = ){1,19}(\"a= b\"){1,22}IWS(\n){1,28}SEE(br){2,1}]",
            "[T(Hello, ){1,1}SE(br[th:text='a= b']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br  th:text=\"ll\"/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS(  ){1,11}A(th:text){1,13}(=){1,20}(\"ll\"){1,21}SEE(br){1,25}]",
            "[T(Hello, ){1,1}SE(br[th:text='ll']){1,8}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello, <br \nth:text=\"ll\"/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( \n){1,11}A(th:text){2,1}(=){2,8}(\"ll\"){2,9}SEE(br){2,13}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "Hello, World! <br/>\n<div\n l\n     a=\"12 3\" zas    o=\"\"  b=\"lelo\n  = s\">lala</div> <p th=\"lala\" >liool</p>",
            "[T(Hello, World! ){1,1}SES(br){1,15}SEE(br){1,18}T(\n){1,20}" +
              "OES(div){2,1}IWS(\n ){2,5}A(l){3,2}(){3,3}(){3,3}IWS(\n     ){3,3}" +
              "A(a){4,6}(=){4,7}(\"12 3\"){4,8}IWS( ){4,14}A(zas){4,15}(){4,18}(){4,18}IWS(    ){4,18}" +
              "A(o){4,22}(=){4,23}(\"\"){4,24}IWS(  ){4,26}A(b){4,28}(=){4,29}(\"lelo\n  = s\"){4,30}" +
              "OEE(div){5,7}T(lala){5,8}CES(div){5,12}CEE(div){5,17}T( ){5,18}" +
              "OES(p){5,19}IWS( ){5,21}A(th){5,22}(=){5,24}(\"lala\"){5,25}IWS( ){5,31}OEE(p){5,32}" +
              "T(liool){5,33}CES(p){5,38}CEE(p){5,41}]",
              null,
              noRestrictionsAutoClose);

        testDoc(
            "Hello<!--hi!-->, <br/>",
            "[T(Hello){1,1}C(hi!){1,6}T(, ){1,16}SES(br){1,18}SEE(br){1,21}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "Hello<!--hi\"!-->, <br/>",
            "[T(Hello){1,1}C(hi\"!){1,6}T(, ){1,17}SES(br){1,19}SEE(br){1,22}]",
            null,
            noRestrictionsAutoClose);

        testDoc(
            "Hello<!-- 4 > 3 -->, <br/>",
            "[T(Hello){1,1}C( 4 > 3 ){1,6}T(, ){1,20}SES(br){1,22}SEE(br){1,25}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "Hello<!-- 4 > 3 > 10 -->, <br/>",
            "[T(Hello){1,1}C( 4 > 3 > 10 ){1,6}T(, ){1,25}SES(br){1,27}SEE(br){1,30}]",
            "[T(Hello){1,1}C( 4 > 3 > 10 ){1,6}T(, ){1,25}SE(br){1,27}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello<!-- 4 > 3\n > 10 -->, <br/>",
            "[T(Hello){1,1}C( 4 > 3\n > 10 ){1,6}T(, ){2,10}SES(br){2,12}SEE(br){2,15}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "Hello<![CDATA[ 4 > 3\n > 10 ]]>, <br/>",
            "[T(Hello){1,1}CD( 4 > 3\n > 10 ){1,6}T(, ){2,10}SES(br){2,12}SEE(br){2,15}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "Hello<![CDATA[ 4 > 3\n \"> 10 ]]>, <br/>",
            "[T(Hello){1,1}CD( 4 > 3\n \"> 10 ){1,6}T(, ){2,11}SES(br){2,13}SEE(br){2,16}]",
            "[T(Hello){1,1}CD( 4 > 3\n \"> 10 ){1,6}T(, ){2,11}SE(br){2,13}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello<![CDATA[ 4 > 3\n '> 10 ]]>, <br/>",
            "[T(Hello){1,1}CD( 4 > 3\n '> 10 ){1,6}T(, ){2,11}SES(br){2,13}SEE(br){2,16}]",
            "[T(Hello){1,1}CD( 4 > 3\n '> 10 ){1,6}T(, ){2,11}SE(br){2,13}]",
            noRestrictionsAutoClose);
        testDoc(
            "Hello<![CDATA[ 4 > 3 > 10 ]]>, <br/>",
            "[T(Hello){1,1}CD( 4 > 3 > 10 ){1,6}T(, ){1,30}SES(br){1,32}SEE(br){1,35}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "Hello<![CDATA[ 4 > 3\n\n\n\n > 10 ]]>, <br/>",
            "[T(Hello){1,1}CD( 4 > 3\n\n\n\n > 10 ){1,6}T(, ){5,10}SES(br){5,12}SEE(br){5,15}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "Hello<![CDATA[ 4 > 3\n\n  \n   \n   \t> 10 ]]>, <br/>",
            "[T(Hello){1,1}CD( 4 > 3\n\n  \n   \n   \t> 10 ){1,6}T(, ){5,13}SES(br){5,15}SEE(br){5,18}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "Hello<![CDATA[ 4 > 3\n\n  \n   \n   \t> 10 ]]>, <br/>\n" +
            "Hello<![CDATA[ 4 > 3\n\n  \n   \n   \t> 10 ]]>, <br/>\n" +
            "Hello<![CDATA[ 4 > 3\n\n  \n   \n   \t> 10 ]]>, <br/>\n" +
            "Hello<![CDATA[ 4 > 3\n\n  \n   \n   \t> 10 ]]>, <br/>\n" +
            "Hello<![CDATA[ 4 > 3\n\n  \n   \n   \t> 10 ]]>, <br/>",
            "[T(Hello){1,1}CD( 4 > 3\n\n  \n   \n   \t> 10 ){1,6}T(, ){5,13}SES(br){5,15}SEE(br){5,18}" +
            "T(\nHello){5,20}CD( 4 > 3\n\n  \n   \n   \t> 10 ){6,6}T(, ){10,13}SES(br){10,15}SEE(br){10,18}" +
            "T(\nHello){10,20}CD( 4 > 3\n\n  \n   \n   \t> 10 ){11,6}T(, ){15,13}SES(br){15,15}SEE(br){15,18}" +
            "T(\nHello){15,20}CD( 4 > 3\n\n  \n   \n   \t> 10 ){16,6}T(, ){20,13}SES(br){20,15}SEE(br){20,18}" +
            "T(\nHello){20,20}CD( 4 > 3\n\n  \n   \n   \t> 10 ){21,6}T(, ){25,13}SES(br){25,15}SEE(br){25,18}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh",
            "[T(kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh){1,1}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh",
            "[T(" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "){1,1}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp;<p> aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\"</p> khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh",
            "[T(" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp;){1,1}OES(p){11,12}OEE(p){11,14}T( aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\"){11,15}CES(p){22,41}CEE(p){22,44}T( khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
            "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
            "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
            "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
            "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
            "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
            "){22,45}]",
            null,
            noRestrictionsAutoClose);

        testDocError(
            "Hello, <p>lala</p>",
            null, null,
            4, 5,
            1, 4,
            noRestrictionsAutoClose);

        testDocError(
            "Hello, <!--lala-->",
            null, null,
            4, 8,
            1, 4,
            noRestrictionsAutoClose);

        testDoc(
            "Hello, <![CDATA[lala]]>",
            "[T(o, <![CD){1,1}]",
            null,
            4, 8,
            noRestrictionsAutoClose);

        testDocError(
            "Hello, <![CDATA[lala]]>",
            null, null,
            4, 12,
            1, 4,
            noRestrictionsAutoClose);

        testDocError(
            "Hello, <br th:text = \"a= b/>",
            null, null,
            1, 8,
            noRestrictionsAutoClose);

        testDoc(
            "<div class = \"lala\">",
            "[OES(div){1,1}IWS( ){1,5}A(class){1,6}( = ){1,11}(\"lala\"){1,14}OEE(div){1,20}ACES(div){1,21}ACEE(div){1,21}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "<div class \n\n= \nlala li=\nlla>",
            "[OES(div){1,1}IWS( ){1,5}A(class){1,6}( \n\n= \n){1,11}(lala){4,1}IWS( ){4,5}A(li){4,6}(=\n){4,8}(lla){5,1}OEE(div){5,4}ACES(div){5,5}ACEE(div){5,5}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "<div class \n\n= \n\"lala\"li=\nlla>",
            "[OES(div){1,1}IWS( ){1,5}A(class){1,6}( \n\n= \n){1,11}(\"lala\"){4,1}A(li){4,7}(=\n){4,9}(lla){5,1}OEE(div){5,4}ACES(div){5,5}ACEE(div){5,5}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "<div class \n\n= \n'lala'li=\nlla>",
            "[OES(div){1,1}IWS( ){1,5}A(class){1,6}( \n\n= \n){1,11}('lala'){4,1}A(li){4,7}(=\n){4,9}(lla){5,1}OEE(div){5,4}ACES(div){5,5}ACEE(div){5,5}]",
            null,
            noRestrictionsAutoClose);


        testDoc(
            "<!DOCTYPE>",
            "[DT(DOCTYPE){1,3}(){1,10}(){1,10}(){1,10}(){1,10}(){1,10}]",
            "[DT()()()(){1,1}]",
            noRestrictionsAutoClose);
        testDoc(
            "<!doctype>",
            "[DT(doctype){1,3}(){1,10}(){1,10}(){1,10}(){1,10}(){1,10}]",
            "[DT()()()(){1,1}]",
            noRestrictionsAutoClose);
        testDocError(
            "<!doctype>",
            null, null, 1, 1,
            noRestrictionsUpperCaseDocType);
        testDocError(
            "<!DOCTYPE html system \"lala\">",
            null, null, 1, 1,
            noRestrictionsUpperCaseDocType);
        testDoc(
            "<!DOCTYPE  >",
            "[DT(DOCTYPE){1,3}(){1,10}(){1,10}(){1,10}(){1,10}(){1,10}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE html>",
            "[DT(DOCTYPE){1,3}(html){1,11}(){1,15}(){1,15}(){1,15}(){1,15}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE  \nhtml>",
            "[DT(DOCTYPE){1,3}(html){2,1}(){2,5}(){2,5}(){2,5}(){2,5}]",
            "[DT(html)()()(){1,1}]",
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE html >",
            "[DT(DOCTYPE){1,3}(html){1,11}(){1,16}(){1,16}(){1,16}(){1,16}]",
            "[DT(html)()()(){1,1}]",
            noRestrictionsAutoClose);
        testDocError(
            "<!DOCTYPE html \"lalero\">",
            null, null,
            1,1,
            noRestrictionsAutoClose);
        testDocError(
            "<!DOCTYPE html lalero>",
            null, null,
            1,1,
            noRestrictionsAutoClose);
        testDocError(
            "<!DOCTYPE html lalero>",
            null, null,
            1,1,
            noRestrictionsAutoClose);
        testDocError(
            "<!DOCTYPE html \"lalero\">",
            null, null,
            1,1,
            noRestrictionsAutoClose);
        testDocError(
            "<!DOCTYPE html \"lalero\"  >",
            null, null,
            1,1,
            noRestrictionsAutoClose);
        testDocError(
            "<!DOCTYPE html \"lalero>",
            null, null,
            1, 1,
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE html PUBLIC \"lalero\">",
            "[DT(DOCTYPE){1,3}(html){1,11}(PUBLIC){1,16}(lalero){1,23}(){1,31}(){1,31}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE html PUBLIC 'lalero'>",
            "[DT(DOCTYPE){1,3}(html){1,11}(PUBLIC){1,16}(lalero){1,23}(){1,31}(){1,31}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE html SYSTEM \"lalero\">",
            "[DT(DOCTYPE){1,3}(html){1,11}(SYSTEM){1,16}(){1,23}(lalero){1,23}(){1,31}]",
            "[DT(html)()(lalero)(){1,1}]",
            noRestrictionsAutoClose);
        testDocError(
            "<!DOCTYPE html PUBLIC lalero>",
            null, null,
            1,1,
            noRestrictionsAutoClose);
        testDocError(
            "<!DOCTYPE html PUBLIC lalero   as>",
            null, null,
            1,1,
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE html PUBLIC \"lalero\">",
            "[DT(DOCTYPE){1,3}(html){1,11}(PUBLIC){1,16}(lalero){1,23}(){1,31}(){1,31}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE html system \"lalero\"  >",
            "[DT(DOCTYPE){1,3}(html){1,11}(system){1,16}(){1,23}(lalero){1,23}(){1,33}]",
            "[DT(html)()(lalero)(){1,1}]",
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE html public \"lalero\"   \n\"hey\">",
            "[DT(DOCTYPE){1,3}(html){1,11}(public){1,16}(lalero){1,23}(hey){2,1}(){2,6}]",
            "[DT(html)(lalero)(hey)(){1,1}]",
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE html public 'lalero'   \n'hey'>",
            "[DT(DOCTYPE){1,3}(html){1,11}(public){1,16}(lalero){1,23}(hey){2,1}(){2,6}]",
            "[DT(html)(lalero)(hey)(){1,1}]",
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE html public \"lalero\n\"   \n\"hey\">",
            "[DT(DOCTYPE){1,3}(html){1,11}(public){1,16}(lalero\n){1,23}(hey){3,1}(){3,6}]",
            "[DT(html)(lalero\n)(hey)(){1,1}]",
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE html system \n\"lalero\"\"le\">",
            "[DT(DOCTYPE){1,3}(html){1,11}(system){1,16}(){2,1}(lalero\"\"le){2,1}(){2,13}]",
            null,
            noRestrictionsAutoClose);
        testDocError(
            "<!DOCTYPE html system \n\"lalero\" \"le\">",
            null, null,
            1,1,
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE html system \n\"lalero\" [somethinghere]>",
            "[DT(DOCTYPE){1,3}(html){1,11}(system){1,16}(){2,1}(lalero){2,1}(somethinghere){2,10}]",
            "[DT(html)()(lalero)(somethinghere){1,1}]",
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE html public \n\"lalero\" [somethinghere]>",
            "[DT(DOCTYPE){1,3}(html){1,11}(public){1,16}(lalero){2,1}(){2,10}(somethinghere){2,10}]",
            "[DT(html)(lalero)()(somethinghere){1,1}]",
            noRestrictionsAutoClose);
        testDocError(
            "<!DOCTYPE html public \n\"lalero\" asas [somethinghere]>",
            null, null,
            1,1,
            noRestrictionsAutoClose);
        testDocError(
            "<!DOCTYPE html system \n\"lalero\" asas [somethinghere]>",
            null, null,
            1,1,
            noRestrictionsAutoClose);
        testDocError(
            "<!DOCTYPE html system \n\"lalero\" \"asas\" [somethinghere]>",
            null, null,
            1,1,
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE html public \n\"lalero\" \"asas\" [somethinghere]>",
            "[DT(DOCTYPE){1,3}(html){1,11}(public){1,16}(lalero){2,1}(asas){2,10}(somethinghere){2,17}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE html public \n\"lalero\" \"asas\" \n\n[somethinghere]\n  >",
            "[DT(DOCTYPE){1,3}(html){1,11}(public){1,16}(lalero){2,1}(asas){2,10}(somethinghere){4,1}]",
            "[DT(html)(lalero)(asas)(somethinghere){1,1}]",
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE sgml public \"lele\" [\n <!ELEMENT sgml ANY>\n  <!ENTITY % std       \"standard SGML\">\n ]>",
            "[DT(DOCTYPE){1,3}(sgml){1,11}(public){1,16}(lele){1,23}(){1,30}(\n <!ELEMENT sgml ANY>\n  <!ENTITY % std       \"standard SGML\">\n ){1,30}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE sgml [\n <!ELEMENT sgml ANY>\n  <!ENTITY % std       \"standard SGML\">\n ]>",
            "[DT(DOCTYPE){1,3}(sgml){1,11}(){1,16}(){1,16}(){1,16}(\n <!ELEMENT sgml ANY>\n  <!ENTITY % std       \"standard SGML\">\n ){1,16}]",
            "[DT(sgml)()()(\n <!ELEMENT sgml ANY>\n  <!ENTITY % std       \"standard SGML\">\n ){1,1}]",
            noRestrictionsAutoClose);
        testDocError(
            "<!DOCTYPE sgml public [\n <!ELEMENT sgml ANY>\n  <!ENTITY % std       \"standard SGML\">\n ]>",
            null, null,
            1,1,
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE sgml public \"lele\" [\n <!ELEMENT sgml ANY>\n <!-- this is a comment inside --> <!ENTITY % std       \"standard SGML\">\n ]>",
            "[DT(DOCTYPE){1,3}(sgml){1,11}(public){1,16}(lele){1,23}(){1,30}(\n <!ELEMENT sgml ANY>\n <!-- this is a comment inside --> <!ENTITY % std       \"standard SGML\">\n ){1,30}]",
            null,
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE sgml system \"lele\" [\n <!ELEMENT sgml ANY>\n <!-- this is a comment inside --> <!ENTITY % std       \"standard SGML\">\n ]>",
            "[DT(DOCTYPE){1,3}(sgml){1,11}(system){1,16}(){1,23}(lele){1,23}(\n <!ELEMENT sgml ANY>\n <!-- this is a comment inside --> <!ENTITY % std       \"standard SGML\">\n ){1,30}]",
            "[DT(sgml)()(lele)(\n <!ELEMENT sgml ANY>\n <!-- this is a comment inside --> <!ENTITY % std       \"standard SGML\">\n ){1,1}]",
            noRestrictionsAutoClose);
        testDocError(
            "<!DOCTYPE sgml public \"lele\" [\n <!ELEMENT sgml [ ANY>\n <!-- this is a comment inside --> <!ENTITY % std       \"standard SGML\">\n ]>",
            null, null,
            1,1,
            noRestrictionsAutoClose);
        testDoc(
            "<!DOCTYPE sgml public \"lele\" [\n <!ELEMENT sgml [ ANY>]\n <!-- this is a comment inside --> <!ENTITY % std       \"standard SGML\">\n ]>",
            "[DT(DOCTYPE){1,3}(sgml){1,11}(public){1,16}(lele){1,23}(){1,30}(\n <!ELEMENT sgml [ ANY>]\n <!-- this is a comment inside --> <!ENTITY % std       \"standard SGML\">\n ){1,30}]",
            "[DT(sgml)(lele)()(\n <!ELEMENT sgml [ ANY>]\n <!-- this is a comment inside --> <!ENTITY % std       \"standard SGML\">\n ){1,1}]",
            noRestrictionsAutoClose);

        testDoc(
            "<?xml version=\"1.0\"?>",
            "[XD(xml){1,3}(1.0){1,15}(){1,20}(){1,20}]",
            "[XD(1.0)()(){1,1}]",
            noRestrictionsAutoClose);

        testDoc(
            "<?xml version=\"1.0\" encoding=\"\"?>",
            "[XD(xml){1,3}(1.0){1,15}(){1,30}(){1,32}]",
            "[XD(1.0)()(){1,1}]",
            noRestrictionsAutoClose);

        testDoc(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
            "[XD(xml){1,3}(1.0){1,15}(UTF-8){1,30}(){1,37}]",
            "[XD(1.0)(UTF-8)(){1,1}]",
            noRestrictionsAutoClose);

        testDoc(
            "<?xml version=\"1.0\"   \nencoding=\"UTF-8\"   ?>",
            "[XD(xml){1,3}(1.0){1,15}(UTF-8){2,10}(){2,20}]",
            "[XD(1.0)(UTF-8)(){1,1}]",
            noRestrictionsAutoClose);

        testDoc(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>",
            "[XD(xml){1,3}(1.0){1,15}(UTF-8){1,30}(yes){1,49}]",
            "[XD(1.0)(UTF-8)(yes){1,1}]",
            noRestrictionsAutoClose);

        testDocError(
            "<?xml version=\"1.0\" standalone=\"yes\" encoding=\"UTF-8\"?>",
            null, null, 1, 1,
            noRestrictionsAutoClose);

        testDocError(
            "<?xml standalone=\"yes\" version=\"1.0\" encoding=\"UTF-8\"?>",
            null, null, 1, 1,
            noRestrictionsAutoClose);

        testDocError(
            "<?xml versio=\"1.0\"?>",
            null, null, 1, 1,
            noRestrictionsAutoClose);

        testDocError(
            "<?xml?>",
            null, null, 1, 1,
            noRestrictionsAutoClose);

        testDocError(
            "<?xml  ?>",
            null, null, 1, 1,
            noRestrictionsAutoClose);

        testDoc(
            "<?XML version=\"1.0\"?>",
            "[P(XML){1,3}(version=\"1.0\"){1,7}]",
            "[P(XML)(version=\"1.0\"){1,1}]",
            noRestrictionsAutoClose);

        testDocError(
            "<?xml Version=\"1.0\"?>",
            null, null, 1, 1,
            noRestrictionsAutoClose);

        testDoc(
            "<?xml version=\"1.0\"  ?>",
            "[XD(xml){1,3}(1.0){1,15}(){1,22}(){1,22}]",
            "[XD(1.0)()(){1,1}]",
            noRestrictionsAutoClose);

        testDoc(
            "<?xml version=\"1.0\"?><!DOCTYPE html>",
            "[XD(xml){1,3}(1.0){1,15}(){1,20}(){1,20}DT(DOCTYPE){1,24}(html){1,32}(){1,36}(){1,36}(){1,36}(){1,36}]",
            "[XD(1.0)()(){1,1}DT(html)()()(){1,22}]",
            noRestrictionsAutoClose);
        testDoc(
            "<?xml version=\"1.0\"?>\n<!DOCTYPE html>",
            "[XD(xml){1,3}(1.0){1,15}(){1,20}(){1,20}T(\n){1,22}DT(DOCTYPE){2,3}(html){2,11}(){2,15}(){2,15}(){2,15}(){2,15}]",
            "[XD(1.0)()(){1,1}T(\n){1,22}DT(html)()()(){2,1}]",
            noRestrictionsAutoClose);

        testDoc(
            "\n <!ELEMENT sgml ANY>",
            "[T(\n ){1,1}OES(!ELEMENT){2,2}IWS( ){2,11}A(sgml){2,12}(){2,16}(){2,16}IWS( ){2,16}A(ANY){2,17}(){2,20}(){2,20}OEE(!ELEMENT){2,20}ACES(!ELEMENT){2,21}ACEE(!ELEMENT){2,21}]",
            "[T(\n ){1,1}OE(!ELEMENT[sgml='',ANY='']){2,2}ACE(!ELEMENT){2,21}]",
            noRestrictionsAutoClose);
        testDoc(
            "\n <!ELEMENT sgml ANY>\n <!-- this is a comment inside --> <!ENTITY % std       \"standard SGML\">\n",
            "[T(\n ){1,1}OES(!ELEMENT){2,2}IWS( ){2,11}A(sgml){2,12}(){2,16}(){2,16}IWS( ){2,16}A(ANY){2,17}(){2,20}(){2,20}OEE(!ELEMENT){2,20}T(\n ){2,21}C( this is a comment inside ){3,2}T( ){3,35}OES(!ENTITY){3,36}IWS( ){3,44}A(%){3,45}(){3,46}(){3,46}IWS( ){3,46}A(std){3,47}(){3,50}(){3,50}IWS(       ){3,50}A(\"standard){3,57}(){3,66}(){3,66}IWS( ){3,66}A(SGML\"){3,67}(){3,72}(){3,72}OEE(!ENTITY){3,72}T(\n){3,73}ACES(!ENTITY){4,1}ACEE(!ENTITY){4,1}ACES(!ELEMENT){4,1}ACEE(!ELEMENT){4,1}]",
            "[T(\n ){1,1}OE(!ELEMENT[sgml='',ANY='']){2,2}T(\n ){2,21}C( this is a comment inside ){3,2}T( ){3,35}OE(!ENTITY[%='',std='',\"standard='',SGML\"='']){3,36}T(\n){3,73}ACE(!ENTITY){4,1}ACE(!ELEMENT){4,1}]",
            noRestrictionsAutoClose);


        testDoc(
            "<?xsl-stylesheet a=\"1\"?>",
            "[P(xsl-stylesheet){1,3}(a=\"1\"){1,18}]",
            "[P(xsl-stylesheet)(a=\"1\"){1,1}]",
            noRestrictionsAutoClose);
        testDoc(
            "<?xsl-stylesheet ?>",
            "[P(xsl-stylesheet){1,3}(){1,18}]",
            "[P(xsl-stylesheet)(){1,1}]",
            noRestrictionsAutoClose);
        testDoc(
            "<?xsl-stylesheet?>",
            "[P(xsl-stylesheet){1,3}(){1,17}]",
            "[P(xsl-stylesheet)(){1,1}]",
            noRestrictionsAutoClose);
        testDoc(
            "<?xsl-stylesheet a=\"1\" a b > uas23 ?>",
            "[P(xsl-stylesheet){1,3}(a=\"1\" a b > uas23 ){1,18}]",
            "[P(xsl-stylesheet)(a=\"1\" a b > uas23 ){1,1}]",
            noRestrictionsAutoClose);
        testDoc(
            "<p><!--a--></p>",
            "[OES(p){1,1}OEE(p){1,3}C(a){1,4}CES(p){1,12}CEE(p){1,15}]",
            "[OE(p){1,1}C(a){1,4}CE(p){1,12}]",
            noRestrictionsAutoClose);
        testDoc(
            "<p><!--a-->",
            "[OES(p){1,1}OEE(p){1,3}C(a){1,4}ACES(p){1,12}ACEE(p){1,12}]",
            "[OE(p){1,1}C(a){1,4}ACE(p){1,12}]",
            noRestrictionsAutoClose);
        testDoc(
            "<p><?xsl-stylesheet a=\"1\" a b > uas23 ?>",
            "[OES(p){1,1}OEE(p){1,3}P(xsl-stylesheet){1,6}(a=\"1\" a b > uas23 ){1,21}ACES(p){1,41}ACEE(p){1,41}]",
            "[OE(p){1,1}P(xsl-stylesheet)(a=\"1\" a b > uas23 ){1,4}ACE(p){1,41}]",
            noRestrictionsAutoClose);

        testDoc(
            "<p>Hello</p>",
            "[OES(p){1,1}OEE(p){1,3}T(Hello){1,4}CES(p){1,9}CEE(p){1,12}]",
            "[OE(p){1,1}T(Hello){1,4}CE(p){1,9}]",
            wellFormedXml);
        testDoc(
            "<h1>Hello</h1>",
            "[OES(h1){1,1}OEE(h1){1,4}T(Hello){1,5}CES(h1){1,10}CEE(h1){1,14}]",
            "[OE(h1){1,1}T(Hello){1,5}CE(h1){1,10}]",
            wellFormedXml);
        testDocError(
            "<p>Hello</h1>",
            null, null, 1, 9,
            wellFormedXml);
        testDocError(
            "<p>Hello",
            null, null, -1, -1,
            wellFormedXml);
        testDocError(
            "Hello</h1>",
            null, null, 1, 6,
            wellFormedXml);
        testDoc(
            "<h1>Hello</h1 >",
            "[OES(h1){1,1}OEE(h1){1,4}T(Hello){1,5}CES(h1){1,10}IWS( ){1,14}CEE(h1){1,15}]",
            "[OE(h1){1,1}T(Hello){1,5}CE(h1){1,10}]",
            wellFormedXml);

        testDoc(
            "<?xml version=\"1.0\"?>\n<!DOCTYPE html>\n<html></html>",
            "[XD(xml){1,3}(1.0){1,15}(){1,20}(){1,20}T(\n){1,22}DT(DOCTYPE){2,3}(html){2,11}(){2,15}(){2,15}(){2,15}(){2,15}T(\n){2,16}OES(html){3,1}OEE(html){3,6}CES(html){3,7}CEE(html){3,13}]",
            "[XD(1.0)()(){1,1}T(\n){1,22}DT(html)()()(){2,1}T(\n){2,16}OE(html){3,1}CE(html){3,7}]",
            wellFormedXml);
        testDoc(
            "<?xml version=\"1.0\"?>\n<html></html>",
            "[XD(xml){1,3}(1.0){1,15}(){1,20}(){1,20}T(\n){1,22}OES(html){2,1}OEE(html){2,6}CES(html){2,7}CEE(html){2,13}]",
            "[XD(1.0)()(){1,1}T(\n){1,22}OE(html){2,1}CE(html){2,7}]",
            wellFormedXml);
        testDoc(
            "<!DOCTYPE html>\n<html></html>",
            "[DT(DOCTYPE){1,3}(html){1,11}(){1,15}(){1,15}(){1,15}(){1,15}T(\n){1,16}OES(html){2,1}OEE(html){2,6}CES(html){2,7}CEE(html){2,13}]",
            "[DT(html)()()(){1,1}T(\n){1,16}OE(html){2,1}CE(html){2,7}]",
            wellFormedXml);
        testDoc(
            "\n<!DOCTYPE html>\n<html></html>",
            "[T(\n){1,1}DT(DOCTYPE){2,3}(html){2,11}(){2,15}(){2,15}(){2,15}(){2,15}T(\n){2,16}OES(html){3,1}OEE(html){3,6}CES(html){3,7}CEE(html){3,13}]",
            "[T(\n){1,1}DT(html)()()(){2,1}T(\n){2,16}OE(html){3,1}CE(html){3,7}]",
            wellFormedXml);
        testDoc(
            "\n<?xml version=\"1.0\"?>\n<!DOCTYPE html>\n<html></html>",
            "[T(\n){1,1}XD(xml){2,3}(1.0){2,15}(){2,20}(){2,20}T(\n){2,22}DT(DOCTYPE){3,3}(html){3,11}(){3,15}(){3,15}(){3,15}(){3,15}T(\n){3,16}OES(html){4,1}OEE(html){4,6}CES(html){4,7}CEE(html){4,13}]",
            "[T(\n){1,1}XD(1.0)()(){2,1}T(\n){2,22}DT(html)()()(){3,1}T(\n){3,16}OE(html){4,1}CE(html){4,7}]",
            wellFormedXml);
        testDoc(
            "<?xml version=\"1.0\"?>\n<!-- a comment -->\n<!DOCTYPE html>\n<html></html>",
            "[XD(xml){1,3}(1.0){1,15}(){1,20}(){1,20}T(\n){1,22}C( a comment ){2,1}T(\n){2,19}DT(DOCTYPE){3,3}(html){3,11}(){3,15}(){3,15}(){3,15}(){3,15}T(\n){3,16}OES(html){4,1}OEE(html){4,6}CES(html){4,7}CEE(html){4,13}]",
            "[XD(1.0)()(){1,1}T(\n){1,22}C( a comment ){2,1}T(\n){2,19}DT(html)()()(){3,1}T(\n){3,16}OE(html){4,1}CE(html){4,7}]",
            wellFormedXml);
        testDoc(
            "<!-- a comment -->\n<?xml version=\"1.0\"?>\n<!DOCTYPE html>\n<html></html>",
            "[C( a comment ){1,1}T(\n){1,19}XD(xml){2,3}(1.0){2,15}(){2,20}(){2,20}T(\n){2,22}DT(DOCTYPE){3,3}(html){3,11}(){3,15}(){3,15}(){3,15}(){3,15}T(\n){3,16}OES(html){4,1}OEE(html){4,6}CES(html){4,7}CEE(html){4,13}]",
            "[C( a comment ){1,1}T(\n){1,19}XD(1.0)()(){2,1}T(\n){2,22}DT(html)()()(){3,1}T(\n){3,16}OE(html){4,1}CE(html){4,7}]",
            wellFormedXml);
        testDoc(
            "<!DOCTYPE html>\n<html><?xml version=\"1.0\"?>\n</html>",
            "[DT(DOCTYPE){1,3}(html){1,11}(){1,15}(){1,15}(){1,15}(){1,15}T(\n){1,16}OES(html){2,1}OEE(html){2,6}XD(xml){2,9}(1.0){2,21}(){2,26}(){2,26}T(\n){2,28}CES(html){3,1}CEE(html){3,7}]",
            "[DT(html)()()(){1,1}T(\n){1,16}OE(html){2,1}XD(1.0)()(){2,7}T(\n){2,28}CE(html){3,1}]",
            noRestrictionsAutoClose);
        testDocError(
            "<!DOCTYPE html>\n<?xml version=\"1.0\"?>\n<html></html>",
            null, null, 2, 1,
            wellFormedXml);
        testDocError(
            "<!DOCTYPE html>\n<html><?xml version=\"1.0\"?>\n</html>",
            null, null, 2, 7,
            wellFormedXml);
        testDocError(
            "<html><?xml version=\"1.0\"?>\n</html>",
            null, null, 1, 7,
            wellFormedXml);
        testDocError(
            "<html><!DOCTYPE html>\n</html>",
            null, null, 1, 7,
            wellFormedXml);
        testDocError(
            "<!DOCTYPE html><!DOCTYPE html>",
            null, null, 1, 16,
            wellFormedXml);
        testDoc(
            "<!DOCTYPE html><!DOCTYPE html>",
            "[DT(DOCTYPE){1,3}(html){1,11}(){1,15}(){1,15}(){1,15}(){1,15}DT(DOCTYPE){1,18}(html){1,26}(){1,30}(){1,30}(){1,30}(){1,30}]",
            null,
            noRestrictionsAutoClose);

        testDoc(
            "Hello, <br th:text=\"ll\"/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}(=){1,19}(\"ll\"){1,20}SEE(br){1,24}]",
            "[T(Hello, ){1,1}SE(br[th:text='ll']){1,8}]",
            wellFormedXml);
        testDoc(
            "Hello, <br th:text='ll'/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}(=){1,19}('ll'){1,20}SEE(br){1,24}]",
            "[T(Hello, ){1,1}SE(br[th:text='ll']){1,8}]",
            wellFormedXml);
        testDocError(
            "Hello, <br th:text=ll/>",
            null, null, 1, 20,
            wellFormedXml);
        testDocError(
            "Hello, <br th:text=/>",
            null, null, 1, 20,
            wellFormedXml);
        testDocError(
            "Hello, <br th:text/>",
            null, null, 1, 19,
            wellFormedXml);
        testDoc(
            "<html></html><html></html>",
            "[OES(html){1,1}OEE(html){1,6}CES(html){1,7}CEE(html){1,13}OES(html){1,14}OEE(html){1,19}CES(html){1,20}CEE(html){1,26}]",
            "[OE(html){1,1}CE(html){1,7}OE(html){1,14}CE(html){1,20}]",
            wellFormedXml);
        testDocError(
            "<!DOCTYPE html><html></html><html></html>",
            null, null, 1, 29,
            wellFormedXml);
        testDocError(
            "<!DOCTYPE html><htmla></htmla>",
            null, null, 1, 16,
            wellFormedXml);
        testDocError(
            "<!DOCTYPE html><htma></htma>",
            null, null, 1, 16,
            wellFormedXml);
        testDocError(
            "<!DOCTYPE html><htma/>",
            null, null, 1, 16,
            wellFormedXml);
        testDoc(
            "Hello, <br th:text=\"ll\" th:text=\"la\"/>",
            "[T(Hello, ){1,1}SES(br){1,8}IWS( ){1,11}A(th:text){1,12}(=){1,19}(\"ll\"){1,20}IWS( ){1,24}A(th:text){1,25}(=){1,32}(\"la\"){1,33}SEE(br){1,37}]",
            "[T(Hello, ){1,1}SE(br[th:text='la']){1,8}]",
            noRestrictionsAutoClose);
        testDocError(
            "Hello, <br th:text=\"ll\" th:text=\"la\"/>",
            null, null, 1, 25,
            wellFormedXml);
        testDocError(
            "<!DOCTYPE html>",
            null, null, -1, -1,
            wellFormedXml);

        testDoc(
                "<h1>Hello</h1>",
                "[OES(h1){1,1}OEE(h1){1,4}T(Hello){1,5}CES(h1){1,10}CEE(h1){1,14}]",
                "[OE(h1){1,1}T(Hello){1,5}CE(h1){1,10}]",
                noUnbalacedClosed);
        testDocError(
                "Hello</h1>",
                null,
                null,
                1, 6,
                noUnbalacedClosed);
        testDoc(
                "<h1>Hello",
                "[OES(h1){1,1}OEE(h1){1,4}T(Hello){1,5}ACES(h1){1,10}ACEE(h1){1,10}]",
                "[OE(h1){1,1}T(Hello){1,5}ACE(h1){1,10}]",
                noRestrictionsAutoClose);
        testDocError(
                "<h2>Hello</h1>",
                null,
                null,
                1, 10,
                noUnbalacedClosed);
        testDoc(
                "<h1><h2>Hello</h1>",
                "[OES(h1){1,1}OEE(h1){1,4}OES(h2){1,5}OEE(h2){1,8}T(Hello){1,9}ACES(h2){1,14}ACEE(h2){1,14}CES(h1){1,14}CEE(h1){1,18}]",
                "[OE(h1){1,1}OE(h2){1,5}T(Hello){1,9}ACE(h2){1,14}CE(h1){1,14}]",
                noUnbalacedClosed);
        testDocError(
                "Hello</h1>",
                null,
                null,
                1, 6,
                noUnbalacedClosed);
        testDoc(
                "<h1><h2>Hello</h2>",
                "[OES(h1){1,1}OEE(h1){1,4}OES(h2){1,5}OEE(h2){1,8}T(Hello){1,9}CES(h2){1,14}CEE(h2){1,18}ACES(h1){1,19}ACEE(h1){1,19}]",
                "[OE(h1){1,1}OE(h2){1,5}T(Hello){1,9}CE(h2){1,14}ACE(h1){1,19}]",
                noUnbalacedClosed);
        testDoc(
                "<h1><h2>Hello<!--a--></h1>",
                "[OES(h1){1,1}OEE(h1){1,4}OES(h2){1,5}OEE(h2){1,8}T(Hello){1,9}C(a){1,14}ACES(h2){1,22}ACEE(h2){1,22}CES(h1){1,22}CEE(h1){1,26}]",
                "[OE(h1){1,1}OE(h2){1,5}T(Hello){1,9}C(a){1,14}ACE(h2){1,22}CE(h1){1,22}]",
                noUnbalacedClosed);
        testDoc(
                "<h1></H1>",
                "[OES(h1){1,1}OEE(h1){1,4}CES(H1){1,5}CEE(H1){1,9}]",
                "[OE(h1){1,1}CE(H1){1,5}]",
                wellFormedXmlCaseInsensitive);
        testDocError(
                "<h1></H1>",
                null, null, 1, 5,
                wellFormedXml);
        testDoc(
                "<!DOCTYPE h1><H1></H1>",
                "[DT(DOCTYPE){1,3}(h1){1,11}(){1,13}(){1,13}(){1,13}(){1,13}OES(H1){1,14}OEE(H1){1,17}CES(H1){1,18}CEE(H1){1,22}]",
                "[DT(h1)()()(){1,1}OE(H1){1,14}CE(H1){1,18}]",
                wellFormedXmlCaseInsensitive);
        testDocError(
                "<!DOCTYPE h1><H1></H1>",
                null, null, 1, 14,
                wellFormedXml);
        testDocError(
                "<a b=\"2\" B=\"3\"/>",
                null, null, 1, 10,
                wellFormedXmlCaseInsensitive);
        testDoc(
                "<a b=\"2\" B=\"3\"/>",
                "[SES(a){1,1}IWS( ){1,3}A(b){1,4}(=){1,5}(\"2\"){1,6}IWS( ){1,9}A(B){1,10}(=){1,11}(\"3\"){1,12}SEE(a){1,15}]",
                "[SE(a[b='2',B='3']){1,1}]",
                wellFormedXml);
        testDoc(
                "<a />",
                "[SES(a){1,1}IWS( ){1,3}SEE(a){1,4}]",
                "[SE(a){1,1}]",
                wellFormedXml);
        testDoc(
                "<!--| something |-->",
                "[C(| something |){1,1}]",
                "[C(| something |){1,1}]",
                wellFormedXml);
        testDoc(
                "<!--|> something <|-->",
                "[C(|> something <|){1,1}]",
                "[C(|> something <|){1,1}]",
                wellFormedXml);
        testDoc(
                "<!--%> something <%-->",
                "[C(%> something <%){1,1}]",
                "[C(%> something <%){1,1}]",
                wellFormedXml);
        testDoc(
                "<@block b=\"2\"/>",
                "[SES(@block){1,1}IWS( ){1,8}A(b){1,9}(=){1,10}(\"2\"){1,11}SEE(@block){1,14}]",
                "[SE(@block[b='2']){1,1}]",
                wellFormedXml);
        testDoc(
                "<@inject b=\"2\"/>",
                "[SES(@inject){1,1}IWS( ){1,9}A(b){1,10}(=){1,11}(\"2\"){1,12}SEE(@inject){1,15}]",
                "[SE(@inject[b='2']){1,1}]",
                wellFormedXml);
        testDoc(
                "</@inject>",
                "[CES(@inject){1,1}CEE(@inject){1,10}]",
                "[CE(@inject){1,1}]",
                noRestrictions);
*/
        System.out.println("TOTAL Test executions: " + totalTestExecutions);
        
        
    }
    
    
    
    static void testDocError(final String input, final String outputBreakDown, final int errorLine, final int errorCol) {
        try {
            testDoc(input, outputBreakDown);
            throw new ComparisonFailure(null, "exception", "no exception");
            
        } catch (final TextParseException e) {
            if (errorLine != -1) {
                assertEquals(Integer.valueOf(errorLine), e.getLine());
            } else {
                assertNull(e.getLine());
            }
            if (errorCol != -1) {
                assertEquals(Integer.valueOf(errorCol), e.getCol());
            } else {
                assertNull(e.getCol());
            }
        }
    }

    
    static void testDocError(final String input, final String outputBreakDown, final int offset, final int len, final int errorLine, final int errorCol) {
        try {
            testDoc(input, outputBreakDown, offset, len);
            throw new ComparisonFailure(null, "exception", "no exception");
            
        } catch (final TextParseException e) {
            if (errorLine != -1 && errorCol != -1) {
                assertEquals(Integer.valueOf(errorLine), e.getLine());
                assertEquals(Integer.valueOf(errorCol), e.getCol());
            } else {
                assertNull(e.getLine());
                assertNull(e.getCol());
            }
        }
    }
    
    
    static void testDoc(final String input, final String outputBreakDown) throws TextParseException {
        testDoc(input.toCharArray(), outputBreakDown, 0, input.length());
    }
    
    static void testDoc(String input, final String outputBreakDown, final int offset, final int len) throws TextParseException {
        testDoc(input.toCharArray(), outputBreakDown, offset, len);
    }
    
    static void testDoc(final String input, final String outputBreakDown, final int bufferSize) throws TextParseException {
        testDoc(input.toCharArray(), outputBreakDown, 0, input.length(), bufferSize);
    }
    
    static void testDoc(String input, final String outputBreakDown, final int offset, final int len, final int bufferSize) throws TextParseException {
        testDoc(input.toCharArray(), outputBreakDown, offset, len, bufferSize);
    }
    
    
    
    
    static void testDoc(final char[] input, final String outputBreakDown, 
            final int offset, final int len) throws TextParseException {

        final int maxBufferSize = 16384;
        for (int bufferSize = 1; bufferSize <= maxBufferSize; bufferSize++) {
            testDoc(input, outputBreakDown, offset, len, bufferSize);
        }
        
    }

    
    static void testDoc(
            final char[] input, 
            final String outputBreakDown,
            final int offset, final int len, final int bufferSize)
            throws TextParseException {

        try {

            final TextParser parser = new TextParser();

            // TEST WITH TRACING HANDLER AND READER
            {

                final TraceBuilderTextHandler traceHandler = new TraceBuilderTextHandler();
                ITextHandler handler = new TextEventProcessorHandler(traceHandler);

                if (offset == 0 && len == input.length) {
                    parser.parseDocument(new CharArrayReader(input), bufferSize, handler);
                } else { 
                    parser.parseDocument(new CharArrayReader(input, offset, len), bufferSize, handler);
                }

                final List<TextTraceEvent> trace = traceHandler.getTrace();
                final StringBuilder strBuilder = new StringBuilder();
                for (final TextTraceEvent event : trace) {
                    if (event.getEventType().equals(TextTraceEvent.EventType.DOCUMENT_START)) {
                        strBuilder.append("[");
                    } else if (event.getEventType().equals(TextTraceEvent.EventType.DOCUMENT_END)) {
                        strBuilder.append("]");
                    } else {
                        strBuilder.append(event);
                    }
                }

                final String result = strBuilder.toString();
                if (outputBreakDown != null) {
                    assertEquals(outputBreakDown, result);
                }
            }

            // TEST WITH TRACING HANDLER AND NO READER WITH PADDING
            {

                final char[] newInput = new char[len + 10];
                newInput[0] = 'X';
                newInput[1] = 'X';
                newInput[2] = 'X';
                newInput[3] = 'X';
                newInput[4] = 'X';
                System.arraycopy(input,offset,newInput,5,len);
                newInput[newInput.length - 1] = 'X';
                newInput[newInput.length - 2] = 'X';
                newInput[newInput.length - 3] = 'X';
                newInput[newInput.length - 4] = 'X';
                newInput[newInput.length - 5] = 'X';

                final TraceBuilderTextHandler traceHandler = new TraceBuilderTextHandler();
                ITextHandler handler = new TextEventProcessorHandler(traceHandler);

                parser.parseDocument(new CharArrayReader(newInput, 5, len), bufferSize, handler);

                final List<TextTraceEvent> trace = traceHandler.getTrace();
                final StringBuilder strBuilder = new StringBuilder();
                for (final TextTraceEvent event : trace) {
                    if (event.getEventType().equals(TextTraceEvent.EventType.DOCUMENT_START)) {
                        strBuilder.append("[");
                    } else if (event.getEventType().equals(TextTraceEvent.EventType.DOCUMENT_END)) {
                        strBuilder.append("]");
                    } else {
                        strBuilder.append(event);
                    }
                }

                final String result = strBuilder.toString();
                if (outputBreakDown != null) {
                    assertEquals(outputBreakDown, result);
                }
            }

            
            totalTestExecutions++;
            
        } catch (final ComparisonFailure cf) {
            System.err.println("Error parsing text \"" + new String(input, offset, len) + "\" with buffer size: " + bufferSize);
            throw cf;
        } catch (final Exception e) {
            throw new TextParseException("Error parsing text \"" + new String(input, offset, len) + "\" with buffer size: " + bufferSize, e);
        }
        
    }

    
}